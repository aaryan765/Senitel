package com.aaryan.senitel.engine

import com.aaryan.senitel.models.Host
import com.aaryan.senitel.utils.expandCIDR
import java.net.InetAddress

class HostDiscovery {

    private val tcpScanner = TcpScanner()

    private val commonPorts = listOf(
        21,
        22,
        23,
        25,
        53,
        80,
        110,
        135,
        139,
        143,
        443,
        445,
        3389,
        8080
    )

    fun discover(target: String): List<Host> {

        return if (target.contains("/")) {
            discoverNetwork(target)
        } else {
            discoverSingleHost(target)
        }

    }

    private fun discoverNetwork(
        cidr: String
    ): List<Host> {

        val hosts = mutableListOf<Host>()

        val addresses = expandCIDR(cidr)

        for (ip in addresses) {

            val host = discoverHost(ip)

            if (host != null) {
                hosts.add(host)
            }

        }

        return hosts

    }

    private fun discoverSingleHost(
        target: String
    ): List<Host> {

        val host = discoverHost(target)

        return if (host != null) {
            listOf(host)
        } else {
            emptyList()
        }

    }

    private fun discoverHost(
        target: String
    ): Host? {

        return try {

            val address = InetAddress.getByName(target)

            val ipAddress = address.hostAddress ?: target

            val hostName = address.hostName ?: "Unknown"

            val openPorts = tcpScanner.scanPorts(
                ipAddress,
                commonPorts,
                150
            )

            if (openPorts.isEmpty()) {

                null

            } else {

                Host(
                    ip = ipAddress,
                    hostname = hostName,
                    reachable = true,
                    macAddress = null,
                    vendor = null,
                    responseTime = null,
                    operatingSystem = null,
                    openPorts = openPorts
                )

            }

        } catch (_: Exception) {

            null

        }

    }

}