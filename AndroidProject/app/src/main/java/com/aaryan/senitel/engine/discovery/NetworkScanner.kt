package com.aaryan.senitel.engine.discovery

import com.aaryan.senitel.utils.expandCIDRToSequence
import com.aaryan.senitel.utils.getCIDRCount

class NetworkScanner {

    fun enumerateHosts(target: String): Sequence<String> {
        return if (target.contains("/")) {
            expandCIDRToSequence(target)
        } else {
            sequenceOf(target)
        }
    }
    
    fun getTotalCount(target: String): Int {
        return if (target.contains("/")) {
            getCIDRCount(target)
        } else {
            1
        }
    }
}
