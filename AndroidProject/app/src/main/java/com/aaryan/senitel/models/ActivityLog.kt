package com.aaryan.senitel.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ActivityLog(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
    val message: String
)
