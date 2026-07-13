package com.aaryan.senitel.engine

import com.aaryan.senitel.models.DiscoveryEvent
import com.aaryan.senitel.utils.ScanType
import kotlinx.coroutines.flow.Flow

class ScanEngine {

    private val hostDiscovery = HostDiscovery()

    fun startScan(
        target: String,
        @Suppress("UNUSED_PARAMETER") scanType: ScanType
    ): Flow<DiscoveryEvent> {
        return hostDiscovery.discover(target)
    }
}
