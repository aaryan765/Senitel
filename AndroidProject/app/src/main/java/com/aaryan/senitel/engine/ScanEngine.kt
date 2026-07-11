package com.aaryan.senitel.engine

import com.aaryan.senitel.utils.ScanType

class ScanEngine {

    private val hostDiscovery = HostDiscovery()

    fun startScan(
        target: String,
        scanType: ScanType
    ): String {

        return when (scanType.name) {

            "HOST DISCOVERY" -> {
                hostDiscovery.discover(target)
            }

            else -> {
                "Scan type not implemented yet."
            }

        }

    }

}