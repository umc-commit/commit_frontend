package com.example.commit.data.model

data class ChatMessage(
    val id: String,
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val type: MessageType,
    val amount: Int?
)
