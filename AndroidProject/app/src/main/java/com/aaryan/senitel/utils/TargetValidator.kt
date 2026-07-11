package com.aaryan.senitel.utils

import java.util.regex.Pattern

fun isValidIPv4(ip: String): Boolean {

    val ipv4Pattern = Pattern.compile(
        "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$"
    )

    return ipv4Pattern.matcher(ip).matches()

}

fun isValidCIDR(cidr: String): Boolean {

    val cidrPattern = Pattern.compile(
        "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)/(3[0-2]|[12]?\\d)$"
    )

    return cidrPattern.matcher(cidr).matches()

}

fun isValidHostname(hostname: String): Boolean {

    val hostnamePattern = Pattern.compile(
        "^[a-zA-Z0-9.-]+$"
    )

    return hostnamePattern.matcher(hostname).matches()

}

