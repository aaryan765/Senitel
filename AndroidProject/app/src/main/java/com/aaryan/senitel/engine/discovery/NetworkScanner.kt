package com.aaryan.senitel.engine.discovery

import com.aaryan.senitel.utils.expandCIDR

class NetworkScanner {

    fun enumerateHosts(
        target: String
    ): List<String> {

        return if (target.contains("/")) {

            expandCIDR(target)

        } else {

            listOf(target)

        }

    }

}

