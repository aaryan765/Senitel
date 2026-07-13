package com.aaryan.senitel.models

sealed class DiscoveryEvent {
    data class Started(val total: Int) : DiscoveryEvent()
    data class Progress(val current: Int, val total: Int) : DiscoveryEvent()
    data class HostFound(val host: Host) : DiscoveryEvent()
    data class Error(val message: String) : DiscoveryEvent()
    data object Completed : DiscoveryEvent()
}
