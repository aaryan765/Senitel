package com.aaryan.senitel.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActivityLog(
    val timestamp: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val message: String
)
