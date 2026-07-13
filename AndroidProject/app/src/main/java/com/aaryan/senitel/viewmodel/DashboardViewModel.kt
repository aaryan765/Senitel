package com.aaryan.senitel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.senitel.engine.ScanEngine
import com.aaryan.senitel.engine.discovery.NetworkScanner
import com.aaryan.senitel.models.Host
import com.aaryan.senitel.models.ScanResult
import com.aaryan.senitel.models.ScanState
import com.aaryan.senitel.utils.ScanType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val scanEngine = ScanEngine()
    private val networkScanner = NetworkScanner()
    private var scanJob: Job? = null

    private val _hosts = MutableStateFlow<List<Host>>(emptyList())
    val hosts: StateFlow<List<Host>> = _hosts.asStateFlow()

    private val _scanResult = MutableStateFlow<ScanResult?>(null)
    val scanResult: StateFlow<ScanResult?> = _scanResult.asStateFlow()

    private val _scanState = MutableStateFlow(ScanState.READY)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _scanStatus = MutableStateFlow("READY")
    val scanStatus: StateFlow<String> = _scanStatus.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    fun startScan(
        target: String,
        scanType: ScanType
    ) {
        if (_scanState.value == ScanState.SCANNING) {
            return
        }

        _hosts.value = emptyList()
        _scanResult.value = null
        _progress.value = 0f
        
        scanJob = viewModelScope.launch(Dispatchers.IO) {
            _scanState.value = ScanState.SCANNING
            _scanStatus.value = "Starting scan..."

            val allAddresses = networkScanner.enumerateHosts(target)
            val totalHosts = if (allAddresses.isEmpty()) 1 else allAddresses.size
            var scannedCount = 0
            val startTime = System.currentTimeMillis()

            try {
                scanEngine.startScan(target, scanType)
                    .onCompletion { cause ->
                        val duration = System.currentTimeMillis() - startTime
                        if (cause == null) {
                            _scanState.value = ScanState.COMPLETED
                            _progress.value = 1f
                            val finalHosts = _hosts.value
                            _scanResult.value = ScanResult(
                                hosts = finalHosts,
                                scanTime = duration,
                                hostsScanned = totalHosts,
                                hostsAlive = finalHosts.size
                            )
                            _scanStatus.value = "Scan Complete: ${finalHosts.size} hosts found"
                        } else if (cause is CancellationException) {
                            _scanState.value = ScanState.READY
                            _scanStatus.value = "Scan Stopped"
                        }
                    }
                    .collect { result ->
                        scannedCount++
                        _progress.value = scannedCount.toFloat() / totalHosts
                        
                        result.host?.let { host ->
                            _hosts.value = _hosts.value + host
                            _scanStatus.value = "Found ${host.ip}..."
                        }
                    }

            } catch (e: Exception) {
                _scanState.value = ScanState.ERROR
                _scanStatus.value = "Scan Failed: ${e.message}"
            }
        }
    }

    fun stopScan() {
        if (_scanState.value == ScanState.SCANNING) {
            _scanState.value = ScanState.STOPPING
            _scanStatus.value = "Stopping..."
            scanJob?.cancel()
        }
    }

    fun reset() {
        stopScan()
        _hosts.value = emptyList()
        _scanResult.value = null
        _scanState.value = ScanState.READY
        _scanStatus.value = "READY"
        _progress.value = 0f
    }
}
