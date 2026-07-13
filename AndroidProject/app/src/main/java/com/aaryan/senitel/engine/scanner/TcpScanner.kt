package com.aaryan.senitel.engine.scanner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class TcpScanner {

    companion object {
        val DISCOVERY_PORTS = listOf(
            22,   // SSH
            80,   // HTTP
            443,  // HTTPS
            445,  // SMB
            548,  // AFP
            631,  // IPP (Printers)
            3389, // RDP
            5000, // Synology/UPnP
            7000, // AirPlay
            8008, // Chromecast
            8009, // Google Cast
            8080, // HTTP Alt
            9100, // PDL (Printers)
            62078 // iPhone/iTunes
        )
    }

    suspend fun isPortOpen(
        ip: String,
        port: Int,
        timeout: Int = 500
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeout)
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun checkDiscoveryPorts(ip: String, timeout: Int = 400): Boolean = coroutineScope {
        DISCOVERY_PORTS.map { port ->
            async { isPortOpen(ip, port, timeout) }
        }.any { it.await() }
    }
    
    suspend fun scanPorts(
        ip: String,
        ports: List<Int>,
        timeout: Int = 300
    ): List<Int> = coroutineScope {
        ports.map { port ->
            async { if (isPortOpen(ip, port, timeout)) port else null }
        }.mapNotNull { it.await() }
    }
}
