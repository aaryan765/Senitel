package com.aaryan.senitel.engine

import com.aaryan.senitel.engine.discovery.NetworkScanner
import com.aaryan.senitel.engine.scanner.TcpScanner
import com.aaryan.senitel.models.Host
import com.aaryan.senitel.models.HostProbeResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

class HostDiscovery {

    private val tcpScanner = TcpScanner()
    private val networkScanner = NetworkScanner()
    
    // Limit parallelism to avoid OS resource exhaustion
    private val discoveryDispatcher = Dispatchers.IO.limitedParallelism(64)

    fun discover(target: String): Flow<HostProbeResult> = channelFlow {
        val addresses = networkScanner.enumerateHosts(target)
        
        val jobs = addresses.map { ip ->
            launch(discoveryDispatcher) {
                val host = probeHost(ip)
                send(HostProbeResult(ip, host))
            }
        }
        
        jobs.joinAll()
    }

    private suspend fun probeHost(ip: String): Host? = withContext(Dispatchers.IO) {
        try {
            val address = InetAddress.getByName(ip)
            
            // 1. Try ICMP (isReachable)
            val isReachable = try { address.isReachable(800) } catch (_: Exception) { false }
            
            // 2. Try common discovery ports
            val hasOpenPorts = tcpScanner.checkDiscoveryPorts(ip, timeout = 500)
            
            if (isReachable || hasOpenPorts) {
                val openPorts = tcpScanner.scanPorts(ip, listOf(22, 80, 443, 445, 8080), 300)
                
                Host(
                    ip = ip,
                    hostname = address.canonicalHostName,
                    reachable = true,
                    macAddress = null,
                    vendor = null,
                    responseTime = null,
                    operatingSystem = null,
                    openPorts = openPorts
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}
