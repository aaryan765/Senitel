package com.aaryan.senitel.engine

import android.util.Log
import com.aaryan.senitel.engine.discovery.NetworkScanner
import com.aaryan.senitel.engine.scanner.PortStatus
import com.aaryan.senitel.engine.scanner.TcpScanner
import com.aaryan.senitel.models.DiscoveryEvent
import com.aaryan.senitel.models.Host
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger

class HostDiscovery {

    private val tcpScanner = TcpScanner()
    private val networkScanner = NetworkScanner()
    
    // Limits the number of concurrent host probes
    private val discoveryDispatcher = Dispatchers.IO.limitedParallelism(64)

    fun discover(target: String): Flow<DiscoveryEvent> = channelFlow {
        val addresses = networkScanner.enumerateHosts(target)
        val totalCount = networkScanner.getTotalCount(target)
        
        send(DiscoveryEvent.Started(totalCount))
        
        val progressCounter = AtomicInteger(0)
        val startTime = System.currentTimeMillis()
        val discoveredIps = mutableSetOf<String>()

        val jobs = addresses.map { ip ->
            launch(discoveryDispatcher) {
                try {
                    val host = probeHost(ip)
                    if (host != null) {
                        synchronized(discoveredIps) {
                            if (discoveredIps.add(ip)) {
                                launch { send(DiscoveryEvent.HostFound(host)) }
                            }
                        }
                    }
                } finally {
                    val currentProgress = progressCounter.incrementAndGet()
                    send(DiscoveryEvent.Progress(currentProgress, totalCount))
                }
            }
        }.toList()
        
        jobs.joinAll()
        
        val duration = System.currentTimeMillis() - startTime
        Log.d("HostDiscovery", "Scan Complete: IPs Scanned=$totalCount, Alive Hosts=${discoveredIps.size}, Time Taken=${duration}ms")
        
        send(DiscoveryEvent.Completed)
    }

    private suspend fun probeHost(ip: String): Host? = withContext(Dispatchers.IO) {
        Log.d("HostDiscovery", "Scanning IP: $ip")
        
        // Stage 1: TCP Connect probes
        if (tcpScanner.checkDiscoveryPorts(ip, timeout = 500)) {
            Log.d("HostDiscovery", "IP $ip: Alive (Reason: Stage 1 - TCP Probe)")
            return@withContext createHost(ip, "TCP Probe")
        }

        // Stage 2: InetAddress.isReachable()
        val address = try { InetAddress.getByName(ip) } catch (_: Exception) { null }
        if (address != null) {
            val isReachable = try { address.isReachable(800) } catch (_: Exception) { false }
            if (isReachable) {
                Log.d("HostDiscovery", "IP $ip: Alive (Reason: Stage 2 - ICMP/isReachable)")
                return@withContext createHost(ip, "ICMP/isReachable", address)
            }
        }

        // Stage 3: Reverse DNS lookup
        if (address != null) {
            val hostname = try { 
                val canonical = address.canonicalHostName
                if (canonical != ip) canonical else null
            } catch (_: Exception) { null }
            
            if (hostname != null) {
                Log.d("HostDiscovery", "IP $ip: Alive (Reason: Stage 3 - Reverse DNS: $hostname)")
                return@withContext createHost(ip, "Reverse DNS", address, hostname)
            }
        }

        // Stage 4: HTTP/HTTPS probe
        if (probeHttp(ip)) {
            Log.d("HostDiscovery", "IP $ip: Alive (Reason: Stage 4 - HTTP Probe)")
            return@withContext createHost(ip, "HTTP Probe")
        }

        // Stage 8: NetBIOS (UDP 137)
        if (probeNetBios(ip)) {
            Log.d("HostDiscovery", "IP $ip: Alive (Reason: Stage 8 - NetBIOS)")
            return@withContext createHost(ip, "NetBIOS")
        }

        // Stage 6 & 7: mDNS / SSDP
        if (probeMdnsSsdp(ip)) {
             Log.d("HostDiscovery", "IP $ip: Alive (Reason: Stage 6/7 - mDNS/SSDP)")
             return@withContext createHost(ip, "mDNS/SSDP")
        }

        null
    }

    private fun probeNetBios(ip: String): Boolean {
        return try {
            DatagramSocket().use { socket ->
                socket.soTimeout = 500
                val address = InetAddress.getByName(ip)
                // Minimal NetBIOS Name Query Request
                val data = byteArrayOf(
                    0x80.toByte(), 0x94.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x01.toByte(),
                    0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
                    0x20.toByte(), 0x43.toByte(), 0x4b.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(),
                    0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(),
                    0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(),
                    0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(),
                    0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x41.toByte(),
                    0x41.toByte(), 0x41.toByte(), 0x41.toByte(), 0x00.toByte(), 0x00.toByte(), 0x21.toByte(),
                    0x00.toByte(), 0x01.toByte()
                )
                val packet = DatagramPacket(data, data.size, address, 137)
                socket.send(packet)
                
                val responseData = ByteArray(1024)
                val responsePacket = DatagramPacket(responseData, responseData.size)
                socket.receive(responsePacket)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun createHost(
        ip: String, 
        reason: String, 
        address: InetAddress? = null, 
        hostname: String? = null
    ): Host {
        Log.d("HostDiscovery", "Creating host for $ip. Reason: $reason")
        val resolvedAddress = address ?: try { InetAddress.getByName(ip) } catch (_: Exception) { null }
        val resolvedHostname = hostname ?: try {
            val h = resolvedAddress?.hostName
            if (h != ip) h else null
        } catch (_: Exception) { null }

        return Host(
            ip = ip,
            hostname = resolvedHostname,
            reachable = true,
            responseTime = null, 
            openPorts = emptyList()
        )
    }

    private fun probeHttp(ip: String): Boolean {
        return try {
            val url = URL("http://$ip/")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 500
            connection.readTimeout = 500
            connection.requestMethod = "HEAD"
            val responseCode = connection.responseCode
            responseCode in 100..599
        } catch (_: Exception) {
            try {
                val url = URL("https://$ip/")
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 500
                connection.readTimeout = 500
                connection.requestMethod = "HEAD"
                val responseCode = connection.responseCode
                responseCode in 100..599
            } catch (_: Exception) {
                false
            }
        }
    }

    private suspend fun probeMdnsSsdp(ip: String): Boolean = withContext(Dispatchers.IO) {
        // This is a very simplified check. Proper mDNS/SSDP would require sending to multicast groups,
        // but here we check if the host responds on those ports (5353 for mDNS, 1900 for SSDP)
        val mdnsStatus = tcpScanner.getPortStatus(ip, 5353, 400)
        val ssdpStatus = tcpScanner.getPortStatus(ip, 1900, 400)
        
        if (mdnsStatus == PortStatus.OPEN || mdnsStatus == PortStatus.REFUSED ||
            ssdpStatus == PortStatus.OPEN || ssdpStatus == PortStatus.REFUSED) {
            return@withContext true
        }

        // Attempting a UDP probe for mDNS/SSDP is better but more complex for a single IP.
        // Usually these are discovered via multicast.
        false
    }
}
