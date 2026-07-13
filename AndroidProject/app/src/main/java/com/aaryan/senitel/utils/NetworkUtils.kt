package com.aaryan.senitel.utils

import java.util.Locale
import kotlin.math.pow

fun expandCIDRToSequence(cidr: String): Sequence<String> = sequence {
    val parts = cidr.split("/")
    if (parts.size != 2) return@sequence

    val baseIp = parts[0]
    val prefix = parts[1].toIntOrNull() ?: return@sequence
    if (prefix !in 0..32) return@sequence

    val ipInt = ipToLong(baseIp) ?: return@sequence
    
    val mask = if (prefix == 0) 0L else (-1L shl (32 - prefix)) and 0xFFFFFFFFL
    val network = ipInt and mask
    val numHosts = 2.0.pow(32 - prefix).toLong()

    val start = if (prefix <= 30) 1 else 0
    val end = if (prefix <= 30) numHosts - 2 else numHosts - 1

    if (start <= end) {
        for (i in start..end) {
            yield(longToIp(network + i))
        }
    }
}

fun getCIDRCount(cidr: String): Int {
    val parts = cidr.split("/")
    if (parts.size != 2) return 1
    val prefix = parts[1].toIntOrNull() ?: return 1
    if (prefix >= 32) return 1
    val numHosts = 2.0.pow(32 - prefix).toLong()
    return if (prefix <= 30) (numHosts - 2).toInt() else numHosts.toInt()
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
