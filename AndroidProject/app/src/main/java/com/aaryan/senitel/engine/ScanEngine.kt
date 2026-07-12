package com.aaryan.senitel.engine

import com.aaryan.senitel.models.Host
import com.aaryan.senitel.utils.ScanType

class ScanEngine {

    private val hostDiscovery = HostDiscovery()

    fun startScan(
        target: String,
        scanType: ScanType
    ): List<Host> {

        return when (scanType.name) {

            "HOST DISCOVERY" -> {
                hostDiscovery.discover(target)
            }

            else -> {
                emptyList()
            }

        }

    }

}