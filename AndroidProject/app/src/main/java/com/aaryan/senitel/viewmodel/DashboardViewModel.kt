package com.aaryan.senitel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.senitel.data.UserPreferencesRepository
import com.aaryan.senitel.engine.ScanEngine
import com.aaryan.senitel.models.*
import com.aaryan.senitel.utils.ScanType
import com.aaryan.senitel.utils.scanTypes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val scanEngine = ScanEngine()
    private val userPrefs = UserPreferencesRepository(application)
    private var scanJob: Job? = null

    private val _hosts = MutableStateFlow<List<Host>>(emptyList())
    val hosts: StateFlow<List<Host>> = _hosts.asStateFlow()

    private val _activityLogs = MutableStateFlow<List<ActivityLog>>(emptyList())
    val activityLogs: StateFlow<List<ActivityLog>> = _activityLogs.asStateFlow()

    private val _scanState = MutableStateFlow(ScanState.READY)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _scanStatus = MutableStateFlow("READY")
    val scanStatus: StateFlow<String> = _scanStatus.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()
    
    private val _scannedCount = MutableStateFlow(0)
    val scannedCount: StateFlow<Int> = _scannedCount.asStateFlow()
    
    private val _totalToScan = MutableStateFlow(0)
    val totalToScan: StateFlow<Int> = _totalToScan.asStateFlow()
    
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime.asStateFlow()

    private val _selectedScanType = MutableStateFlow(scanTypes.first())
    val selectedScanType: StateFlow<ScanType> = _selectedScanType.asStateFlow()

    val operatorName = userPrefs.operatorName.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "AIDEN"
    )

    val targetHistory = userPrefs.targetHistory.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        viewModelScope.launch {
            val lastScanTypeName = userPrefs.lastScanType.first()
            val savedType = scanTypes.find { it.name == lastScanTypeName }
            if (savedType != null) {
                _selectedScanType.value = savedType
            }
        }
    }

    fun updateScanType(scanType: ScanType) {
        _selectedScanType.value = scanType
        viewModelScope.launch {
            userPrefs.updateLastScanType(scanType.name)
        }
    }

    fun updateOperatorName(name: String) {
        viewModelScope.launch {
            userPrefs.updateOperatorName(name)
        }
    }

    fun startScan(target: String, scanType: ScanType) {
        if (_scanState.value == ScanState.SCANNING) return

        resetState()
        addLog("INITIALIZING SCAN: $target")
        
        viewModelScope.launch {
            userPrefs.updateLastTarget(target)
        }

        scanJob = viewModelScope.launch(Dispatchers.IO) {
            _scanState.value = ScanState.SCANNING
            _scanStatus.value = "SCANNING"

            val startTime = System.currentTimeMillis()
            
            val timerJob = launch {
                while (true) {
                    _elapsedTime.value = System.currentTimeMillis() - startTime
                    kotlinx.coroutines.delay(100)
                }
            }

            try {
                scanEngine.startScan(target, scanType)
                    .collect { event ->
                        when (event) {
                            is DiscoveryEvent.Started -> {
                                _totalToScan.value = event.total
                                addLog("NETWORK ENUMERATION COMPLETE: ${event.total} TARGETS")
                            }
                            is DiscoveryEvent.Progress -> {
                                _scannedCount.value = event.current
                                _progress.value = event.current.toFloat() / event.total
                            }
                            is DiscoveryEvent.HostFound -> {
                                _hosts.value = _hosts.value + event.host
                                addLog("NODE DETECTED: ${event.host.ip}")
                            }
                            is DiscoveryEvent.Completed -> {
                                timerJob.cancel()
                                finalizeScan(startTime)
                            }
                            is DiscoveryEvent.Error -> {
                                timerJob.cancel()
                                handleError(event.message)
                            }
                        }
                    }
            } catch (e: CancellationException) {
                timerJob.cancel()
                _scanStatus.value = "STOPPED"
                addLog("SCAN ABORTED BY OPERATOR")
                _scanState.value = ScanState.READY
            } catch (e: Exception) {
                timerJob.cancel()
                handleError(e.localizedMessage ?: "UNKNOWN ERROR")
            }
        }
    }

    private fun addLog(message: String) {
        val currentLogs = _activityLogs.value.toMutableList()
        currentLogs.add(ActivityLog(message = message))
        if (currentLogs.size > 50) currentLogs.removeAt(0)
        _activityLogs.value = currentLogs
    }

    private fun handleError(message: String) {
        _scanState.value = ScanState.ERROR
        _scanStatus.value = "ERROR"
        addLog("CRITICAL FAILURE: $message")
    }

    private fun finalizeScan(startTime: Long) {
        val duration = System.currentTimeMillis() - startTime
        _scanState.value = ScanState.COMPLETED
        _scanStatus.value = "COMPLETED"
        addLog("SCAN SEQUENCE COMPLETE IN ${duration}ms")
        addLog("ALIVE HOSTS: ${_hosts.value.size}")
    }

    private fun resetState() {
        _hosts.value = emptyList()
        _activityLogs.value = emptyList()
        _progress.value = 0f
        _scannedCount.value = 0
        _totalToScan.value = 0
        _elapsedTime.value = 0L
    }

    fun stopScan() {
        if (_scanState.value == ScanState.SCANNING) {
            _scanState.value = ScanState.STOPPING
            _scanStatus.value = "STOPPING"
            scanJob?.cancel()
        }
    }
}
