package com.example.commit.ui.chatroom.bubble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.notoSansKR

@Composable
fun DateHeader(dateText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF2F2F2)) // 연회색 배경
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dateText,
            style = TextStyle(
                fontFamily = notoSansKR,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 0.sp
            ),
            color = Color(0xFFB0B0B0)
        )
    }
}

