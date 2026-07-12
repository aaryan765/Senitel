package com.aaryan.senitel.models

data class Host(

    val ip: String,

    val hostname: String? = null,

    val reachable: Boolean,

    val macAddress: String? = null,

    val vendor: String? = null

)

