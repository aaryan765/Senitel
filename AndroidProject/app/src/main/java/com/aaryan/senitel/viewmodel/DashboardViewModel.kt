package com.aaryan.senitel.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaryan.senitel.engine.ScanEngine
import com.aaryan.senitel.models.Host
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

    private val _scanStatus = MutableStateFlow("READY")
    val scanStatus: StateFlow<String> = _scanStatus.asStateFlow()

    fun startScan(
        target: String,
        scanType: ScanType
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            _scanStatus.value = "Scanning..."

            val results = scanEngine.startScan(
                target,
                scanType
            )

            _hosts.value = results

            _scanStatus.value =
                if (results.isEmpty()) {
                    "No hosts found"
                } else {
                    "Hosts Found: ${results.size}"
                }

        }

    }

}