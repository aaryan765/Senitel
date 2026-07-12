package com.aaryan.senitel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.senitel.engine.ScanEngine
import com.aaryan.senitel.models.Host
import com.aaryan.senitel.models.ScanState
import com.aaryan.senitel.utils.ScanType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val scanEngine = ScanEngine()

    private val _hosts = MutableStateFlow<List<Host>>(emptyList())
    val hosts: StateFlow<List<Host>> = _hosts.asStateFlow()

    private val _scanState = MutableStateFlow(ScanState.READY)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    private val _scanStatus = MutableStateFlow("READY")
    val scanStatus: StateFlow<String> = _scanStatus.asStateFlow()

    fun startScan(
        target: String,
        scanType: ScanType
    ) {

        // Prevent multiple scans from running
        if (_scanState.value == ScanState.SCANNING) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {

            _scanState.value = ScanState.SCANNING
            _scanStatus.value = "Scanning..."

            try {

                val results = scanEngine.startScan(
                    target,
                    scanType
                )

                _hosts.value = results

                _scanState.value = ScanState.COMPLETED

                _scanStatus.value =
                    if (results.isEmpty()) {
                        "No hosts found"
                    } else {
                        "Hosts Found: ${results.size}"
                    }

            } catch (e: Exception) {

                _scanState.value = ScanState.ERROR
                _scanStatus.value = "Scan Failed"

            }

        }

    }

    fun reset() {

        _scanState.value = ScanState.READY
        _scanStatus.value = "READY"

    }

}