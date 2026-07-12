package com.aaryan.senitel.engine

import com.aaryan.senitel.models.Host
import com.aaryan.senitel.utils.ScanType

class ScanEngine {

    private val hostDiscovery = HostDiscovery()

    @Volatile
    private var stopRequested = false

    fun startScan(
        target: String,
        scanType: ScanType
    ): List<Host> {

        stopRequested = false

        return when (scanType.name.uppercase()) {

            "HOST DISCOVERY" -> {

                if (stopRequested) {

                    emptyList()

                } else {

                    hostDiscovery.discover(target)

                }

            }

            else -> {

                emptyList()

            }

        }

    }

    fun stopScan() {

        stopRequested = true

    }

    fun isScanStopped(): Boolean {

        return stopRequested

    }

}