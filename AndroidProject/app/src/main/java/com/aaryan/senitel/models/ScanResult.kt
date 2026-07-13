package com.aaryan.senitel.models

data class ScanResult(

    val hosts: List<Host>,

    val scanTime: Long,

    val hostsScanned: Int,

    val hostsAlive: Int

)

