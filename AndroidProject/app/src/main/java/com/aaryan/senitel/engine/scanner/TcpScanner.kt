package com.aaryan.senitel.engine.scanner

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import java.net.ConnectException
import java.net.SocketTimeoutException

enum class PortStatus {
    OPEN, REFUSED, TIMEOUT, ERROR
}

class TcpScanner {

    companion object {
        val DISCOVERY_PORTS = listOf(22, 53, 80, 443, 445, 8080, 8443)
        
        val TOP_100_PORTS = listOf(
            7, 9, 13, 21, 22, 23, 25, 26, 37, 53, 79, 80, 81, 88, 106, 110, 111, 113, 119, 135, 
            139, 143, 144, 179, 199, 389, 427, 443, 444, 445, 465, 513, 514, 515, 543, 544, 548, 
            554, 587, 631, 646, 873, 990, 993, 995, 1025, 1026, 1027, 1028, 1029, 1110, 1433, 
            1720, 1723, 1755, 1900, 2000, 2001, 2049, 2121, 2717, 3000, 3128, 3306, 3389, 3986, 
            4899, 5000, 5009, 5051, 5060, 5101, 5190, 5357, 5432, 5631, 5666, 5800, 5900, 6000, 
            6001, 6646, 7070, 8000, 8008, 8009, 8080, 8081, 8443, 8888, 9100, 9999, 10000, 32768, 
            49152, 49153, 49154, 49155, 49156, 49157
        )
    }

    suspend fun getPortStatus(
        ip: String,
        port: Int,
        timeout: Int = 500
    ): PortStatus = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), timeout)
                PortStatus.OPEN
            }
        } catch (e: ConnectException) {
            if (e.message?.contains("refused", ignoreCase = true) == true) {
                PortStatus.REFUSED
            } else {
                PortStatus.ERROR
            }
        } catch (e: SocketTimeoutException) {
            PortStatus.TIMEOUT
        } catch (e: Exception) {
            PortStatus.ERROR
        }
    }

    suspend fun checkDiscoveryPorts(ip: String, timeout: Int = 400): Boolean = coroutineScope {
        DISCOVERY_PORTS.map { port ->
            async { 
                val status = getPortStatus(ip, port, timeout)
                status == PortStatus.OPEN || status == PortStatus.REFUSED
            }
        }.any { it.await() }
    }
    
    suspend fun scanPorts(
        ip: String,
        ports: List<Int>,
        timeout: Int = 300
    ): List<Int> = coroutineScope {
        ports.map { port ->
            async { 
                if (getPortStatus(ip, port, timeout) == PortStatus.OPEN) port else null 
            }
        }.mapNotNull { it.await() }
    }

    suspend fun grabBanner(ip: String, port: Int, timeout: Int = 1000): String? = withContext(Dispatchers.IO) {
        try {
            Socket().use { socket ->
                socket.soTimeout = timeout
                socket.connect(InetSocketAddress(ip, port), timeout)
                
                // Generic banner grab
                val outputStream = socket.getOutputStream()
                val inputStream = socket.getInputStream()
                
                // For HTTP, send a simple HEAD request
                if (port == 80 || port == 8080 || port == 8081) {
                    outputStream.write("HEAD / HTTP/1.1\r\nHost: $ip\r\n\r\n".toByteArray())
                }
                
                val buffer = ByteArray(1024)
                val bytesRead = inputStream.read(buffer)
                if (bytesRead != -1) {
                    String(buffer, 0, bytesRead).trim()
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }
}
