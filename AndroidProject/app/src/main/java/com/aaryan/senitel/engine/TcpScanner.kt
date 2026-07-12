package com.aaryan.senitel.engine

import java.net.InetSocketAddress
import java.net.Socket

class TcpScanner {

    fun isPortOpen(
        ip: String,
        port: Int,
        timeout: Int = 1000
    ): Boolean {

        return try {

            Socket().use { socket ->

                socket.connect(
                    InetSocketAddress(ip, port),
                    timeout
                )

                true

            }

        } catch (_: Exception) {

            false

        }

    }

    fun scanPorts(
        ip: String,
        ports: List<Int>,
        timeout: Int = 1000
    ): List<Int> {

        val openPorts = mutableListOf<Int>()

        for (port in ports) {

            if (isPortOpen(ip, port, timeout)) {

                openPorts.add(port)

            }

        }

        return openPorts

    }

}