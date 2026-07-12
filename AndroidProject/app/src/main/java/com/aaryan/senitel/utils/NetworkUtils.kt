package com.aaryan.senitel.utils

fun expandCIDR(cidr: String): List<String> {

    val parts = cidr.split("/")

    if (parts.size != 2) return emptyList()

    val baseIp = parts[0]

    val prefix = parts[1].toIntOrNull() ?: return emptyList()

    if (prefix != 24) return emptyList()

    val octets = baseIp.split(".")

    if (octets.size != 4) return emptyList()

    val network =
        "${octets[0]}.${octets[1]}.${octets[2]}"

    return (1..254).map {

        "$network.$it"

    }

}

