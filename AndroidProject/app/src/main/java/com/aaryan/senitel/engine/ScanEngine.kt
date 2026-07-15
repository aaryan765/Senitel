package com.aaryan.senitel.engine

import com.aaryan.senitel.engine.scanner.TcpScanner
import com.aaryan.senitel.models.DiscoveryEvent
import com.aaryan.senitel.models.Host
import com.aaryan.senitel.utils.ScanType
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.net.InetAddress

class ScanEngine {

    private val hostDiscovery = HostDiscovery()
    private val tcpScanner = TcpScanner()

    fun startScan(
        target: String,
        scanType: ScanType
    ): Flow<DiscoveryEvent> = channelFlow {
        hostDiscovery.discover(target).collect { event ->
            when (event) {
                is DiscoveryEvent.HostFound -> {
                    val enrichedHost = if (scanType.name == "AGGRESSIVE SCAN" || scanType.name == "PORT SCAN") {
                        enrichHost(event.host, scanType)
                    } else {
                        event.host
                    }
                    send(DiscoveryEvent.HostFound(enrichedHost))
                }
                else -> send(event)
            }
        }
    }

    private suspend fun enrichHost(host: Host, scanType: ScanType): Host = withContext(Dispatchers.IO) {
        var currentHost = host
        
        // 1. Port Scanning
        if (scanType.name == "AGGRESSIVE SCAN" || scanType.name == "PORT SCAN") {
            val openPorts = tcpScanner.scanPorts(host.ip, TcpScanner.TOP_100_PORTS)
            currentHost = currentHost.copy(openPorts = openPorts)
        }

        // 2. Service Detection & OS Estimation (for AGGRESSIVE SCAN)
        if (scanType.name == "AGGRESSIVE SCAN") {
            val banners = mutableMapOf<Int, String>()
            currentHost.openPorts.forEach { port ->
                val banner = tcpScanner.grabBanner(host.ip, port)
                if (banner != null) {
                    banners[port] = banner
                }
            }

            // Estimate OS
            val os = estimateOS(currentHost, banners)
            currentHost = currentHost.copy(operatingSystem = os)
            
            // Try to find vendor (simplified)
            val vendor = estimateVendor(currentHost, banners)
            currentHost = currentHost.copy(vendor = vendor)
        }

        currentHost
    }

    private fun estimateOS(host: Host, banners: Map<Int, String>): String {
        // Simple signature matching
        val allBanners = banners.values.joinToString(" ").lowercase()
        
        if (allBanners.contains("ubuntu") || allBanners.contains("debian")) return "Linux (Ubuntu/Debian)"
        if (allBanners.contains("centos") || allBanners.contains("red hat")) return "Linux (CentOS/RHEL)"
        if (allBanners.contains("microsoft-iis") || allBanners.contains("windows")) return "Windows Server"
        if (host.openPorts.contains(3389)) return "Windows"
        if (host.openPorts.contains(445) && host.openPorts.contains(139)) return "Windows"
        if (host.openPorts.contains(548)) return "macOS"
        if (host.openPorts.contains(22) && allBanners.contains("openssh")) return "Linux/Unix"
        
        // Check TTL if we could, but InetAddress.isReachable doesn't expose it.
        // On non-root Android, we can't easily get TTL of incoming packets.
        
        return "Unknown"
    }

    private fun estimateVendor(host: Host, banners: Map<Int, String>): String? {
        val allBanners = banners.values.joinToString(" ").lowercase()
        if (allBanners.contains("apache")) return "Apache Software Foundation"
        if (allBanners.contains("nginx")) return "NGINX, Inc."
        if (allBanners.contains("iis")) return "Microsoft"
        if (host.openPorts.contains(5000) || host.openPorts.contains(5001)) return "Synology"
        if (host.openPorts.contains(8008) || host.openPorts.contains(8009)) return "Google"
        return null
    }
}
