package com.aaryan.senitel.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.senitel.data.UserPreferencesRepository
import com.aaryan.senitel.engine.ScanEngine
import com.aaryan.senitel.models.ActivityLog
import com.aaryan.senitel.models.DiscoveryEvent
import com.aaryan.senitel.models.Host
import com.aaryan.senitel.models.ScanResult
import com.aaryan.senitel.models.ScanState
import com.aaryan.senitel.utils.ScanType
import com.aaryan.senitel.utils.scanTypes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
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

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _selectedScanType = MutableStateFlow(scanTypes.first())
    val selectedScanType: StateFlow<ScanType> = _selectedScanType.asStateFlow()

    val operatorName = userPrefs.operatorName.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "AIDEN"
    )

    fun updateScanType(scanType: ScanType) {
        _selectedScanType.value = scanType
    }

    fun updateOperatorName(name: String) {
        viewModelScope.launch {
            userPrefs.updateOperatorName(name)
        }
    }

    fun startScan(target: String, scanType: ScanType) {
        if (_scanState.value == ScanState.SCANNING) return

        resetState()
        addLog("Scan Started")
        
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
                            }
                            is DiscoveryEvent.Progress -> {
                                _scannedCount.value = event.current
                                _progress.value = event.current.toFloat() / event.total
                            }
                            is DiscoveryEvent.HostFound -> {
                                _hosts.value = _hosts.value + event.host
                                addLog("Host Found: ${event.host.ip}")
                                event.host.openPorts.forEach { port ->
                                    addLog("Port $port Open on ${event.host.ip}")
                                }
                            }
                            is DiscoveryEvent.Completed -> {
                                timerJob.cancel()
                                addLog("Scan Complete")
                                finalizeScan(startTime)
                            }
                            is DiscoveryEvent.Error -> {
                                timerJob.cancel()
                                _scanState.value = ScanState.ERROR
                                _scanStatus.value = "ERROR"
                                addLog("Error: ${event.message}")
                            }
                        }
                    }
            } catch (e: CancellationException) {
                timerJob.cancel()
                _scanState.value = ScanState.READY
                _scanStatus.value = "STOPPED"
                addLog("Scan Stopped")
            } catch (e: Exception) {
                timerJob.cancel()
                _scanState.value = ScanState.ERROR
                _scanStatus.value = "ERROR"
                addLog("Error: ${e.localizedMessage}")
            }
        }
    }

    private fun addLog(message: String) {
        _activityLogs.value = _activityLogs.value + ActivityLog(message = message)
    }

    private fun finalizeScan(startTime: Long) {
        val duration = System.currentTimeMillis() - startTime
        _scanState.value = ScanState.COMPLETED
        _scanStatus.value = "COMPLETED"
        _scanResult.value = ScanResult(
            hosts = _hosts.value,
            scanTime = duration,
            hostsScanned = _scannedCount.value,
            hostsAlive = _hosts.value.size
        )
    }

    private fun resetState() {
        _hosts.value = emptyList()
        _activityLogs.value = emptyList()
        _progress.value = 0f
        _scannedCount.value = 0
        _totalToScan.value = 0
        _elapsedTime.value = 0L
        _scanResult.value = null
    }

    fun stopScan() {
        if (_scanState.value == ScanState.SCANNING) {
            _scanState.value = ScanState.STOPPING
            _scanStatus.value = "STOPPING"
            scanJob?.cancel()
        }
    }

    fun reset() {
        stopScan()
        resetState()
        _scanState.value = ScanState.READY
        _scanStatus.value = "READY"
    }
}
