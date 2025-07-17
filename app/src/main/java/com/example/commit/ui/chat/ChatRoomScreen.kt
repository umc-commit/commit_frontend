package com.example.commit.ui.chat

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun ChatRoomScreen(chatName: String) {
    // UI 구성
    Text(text = "$chatName 님과의 대화방입니다.")
}