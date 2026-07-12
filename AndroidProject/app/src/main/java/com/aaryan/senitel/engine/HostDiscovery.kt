package com.aaryan.senitel.engine

import com.aaryan.senitel.models.Host
import com.aaryan.senitel.utils.expandCIDR
import java.net.InetAddress

class HostDiscovery {

    private val tcpScanner = TcpScanner()

    fun discover(target: String): List<Host> {

        val hosts = mutableListOf<Host>()

        val commonPorts = listOf(
            80,
            443,
            22,
            445
        )

        // Handle CIDR network (example: 192.168.1.0/24)
        if (target.contains("/")) {

            val addresses = expandCIDR(target)

            for (ip in addresses) {

                var reachable = false

                for (port in commonPorts) {

                    if (tcpScanner.isPortOpen(ip, port, 150)) {

                        reachable = true
                        break

                    }

                }

                if (reachable) {

                    try {

                        val address = InetAddress.getByName(ip)

                        hosts.add(
                            Host(
                                ip = address.hostAddress ?: ip,
                                hostname = address.hostName ?: "Unknown",
                                reachable = true,
                                macAddress = null,
                                vendor = null
                            )
                        )

                    } catch (_: Exception) {

                    }

                }

            }

            return hosts

        }

        // Handle single IP or hostname
        try {

            val address = InetAddress.getByName(target)

            var reachable = false

            for (port in commonPorts) {

                if (tcpScanner.isPortOpen(target, port, 1000)) {

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