package com.aaryan.senitel.utils

data class ScanType(
    val name: String,
    val description: String
)

val scanTypes = listOf(

    ScanType(
        "HOST DISCOVERY",
        "Discover live hosts on the selected network."
    ),

    ScanType(
        "PORT SCAN",
        "Scan the target for open TCP/UDP ports."
    ),

    ScanType(
        "SERVICE DETECTION",
        "Identify services running on open ports."
    ),

    ScanType(
        "OS DETECTION",
        "Attempt to identify the target operating system."
    ),

    ScanType(
        "AGGRESSIVE SCAN",
        "Run host discovery, port scan, service detection and OS detection."
    )

)

