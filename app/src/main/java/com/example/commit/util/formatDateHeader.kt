package com.example.commit.util

fun formatDateHeader(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("yyyy년 MM월 dd일", java.util.Locale.KOREAN)
    return formatter.format(java.util.Date(timestamp))
}

