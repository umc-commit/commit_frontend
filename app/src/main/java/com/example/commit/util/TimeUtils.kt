package com.example.commit.util

import java.text.SimpleDateFormat
import java.util.*

fun formatTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}
