package com.aaryan.senitel.engine

import com.aaryan.senitel.models.Host

import java.net.InetAddress

class HostDiscovery {

    private val tcpScanner = TcpScanner()

    fun discover(target: String): List<Host> {

        val hosts = mutableListOf<Host>()

        // If the target is a CIDR network, we'll handle it in the next step.
        if (target.contains("/")) {

            return hosts

        }

        val commonPorts = listOf(
            80,
            443,
            22,
            445
        )

        try {

            val address = InetAddress.getByName(target)

            var reachable = false

            for (port in commonPorts) {

                if (tcpScanner.isPortOpen(target, port)) {

                    reachable = true
                    break

                }

            }

            if (reachable) {

                hosts.add(
                    Host(
                        ip = address.hostAddress ?: target,
                        hostname = address.hostName ?: "Unknown",
                        reachable = true,
                        macAddress = null,
                        vendor = null
                    )
                )

            }

        } catch (_: Exception) {

        }

        return hosts

    }

}