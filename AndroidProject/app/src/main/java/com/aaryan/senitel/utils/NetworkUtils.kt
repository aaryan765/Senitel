package com.aaryan.senitel.utils

import java.util.Locale
import kotlin.math.pow

fun expandCIDR(cidr: String): List<String> {
    val parts = cidr.split("/")
    if (parts.size != 2) return emptyList()

    val baseIp = parts[0]
    val prefix = parts[1].toIntOrNull() ?: return emptyList()
    if (prefix !in 0..32) return emptyList()

    val ipInt = ipToLong(baseIp) ?: return emptyList()
    
    val mask = if (prefix == 0) 0L else (-1L shl (32 - prefix)) and 0xFFFFFFFFL
    val network = ipInt and mask
    val numHosts = 2.0.pow(32 - prefix).toLong()

    // For discovery, we'll generate the list. 
    // Excluding network and broadcast for common subnets
    val start = if (prefix <= 30) 1 else 0
    val end = if (prefix <= 30) numHosts - 2 else numHosts - 1

    val hosts = mutableListOf<String>()
    if (start <= end) {
        for (i in start..end) {
            hosts.add(longToIp(network + i))
        }
    }
    return hosts
}

fun ipToLong(ip: String): Long? {
    return try {
        val octets = ip.split(".")
        if (octets.size != 4) return null
        var result = 0L
        for (i in 0..3) {
            val octet = octets[i].toLong()
            if (octet !in 0..255) return null
            result = result or (octet shl (24 - i * 8))
        }
        result
    } catch (_: Exception) {
        null
    }
}

fun longToIp(ip: Long): String {
    return String.format(
        Locale.US,
        "%d.%d.%d.%d",
        (ip shr 24) and 0xFF,
        (ip shr 16) and 0xFF,
        (ip shr 8) and 0xFF,
        ip and 0xFF
    )
}
