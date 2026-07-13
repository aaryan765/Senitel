package com.aaryan.senitel.engine

import com.aaryan.senitel.engine.discovery.NetworkScanner
import com.aaryan.senitel.engine.scanner.TcpScanner
import com.aaryan.senitel.models.DiscoveryEvent
import com.aaryan.senitel.models.Host
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger

class HostDiscovery {

    private val tcpScanner = TcpScanner()
    private val networkScanner = NetworkScanner()
    
    // Limits the number of concurrent host probes
    private val discoveryDispatcher = Dispatchers.IO.limitedParallelism(32)

    fun discover(target: String): Flow<DiscoveryEvent> = channelFlow {
        val addresses = networkScanner.enumerateHosts(target)
        val totalCount = networkScanner.getTotalCount(target)
        
        send(DiscoveryEvent.Started(totalCount))
        
        val progressCounter = AtomicInteger(0)
        
        val jobs = addresses.map { ip ->
            launch(discoveryDispatcher) {
                try {
                    val host = probeHost(ip)
                    if (host != null) {
                        send(DiscoveryEvent.HostFound(host))
                    }
                } finally {
                    val currentProgress = progressCounter.incrementAndGet()
                    send(DiscoveryEvent.Progress(currentProgress, totalCount))
                }
            }
        }.toList()
        
        jobs.joinAll()
        send(DiscoveryEvent.Completed)
    }

    private suspend fun probeHost(ip: String): Host? = withContext(Dispatchers.IO) {
        try {
            val address = InetAddress.getByName(ip)
            
            // 1. Try ICMP (isReachable)
            val isReachable = try { 
                address.isReachable(700) 
            } catch (_: Exception) { 
                false 
            }
            
            // 2. Try common discovery ports
            val hasOpenPorts = tcpScanner.checkDiscoveryPorts(ip, timeout = 400)
            
            if (isReachable || hasOpenPorts) {
                // If alive, get open ports from the discovery set
                val openPorts = tcpScanner.scanPorts(ip, TcpScanner.DISCOVERY_PORTS, 250)
                
                Host(
                    ip = ip,
                    hostname = if (isReachable) address.hostName else ip, // Avoid blocking canonicalHostName if possible
                    reachable = true,
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
