package com.example.commit.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType

class ChatViewModel : ViewModel() {

    var message by mutableStateOf("")
        private set

    // 채팅 메시지 리스트
    var chatMessages by mutableStateOf(listOf<ChatMessage>())
        private set

    fun onMessageChange(newMessage: String) {
        message = newMessage
    }

    fun sendMessage() {
        if (message.isBlank()) return

        // 새 메시지를 리스트에 추가
        val newChat = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = "me",
            message = message,
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            amount = null
        )

        chatMessages = chatMessages + newChat

        println("보낸 메시지: $message")
        message = ""
    }

    // 결제 요청 등 시스템 메시지도 여기에 추가 가능
    fun sendSystemMessage(text: String) {
        chatMessages = chatMessages + ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = "system",
            message = text,
            timestamp = System.currentTimeMillis(),
            type = MessageType.SYSTEM,
            amount = null
        )
    }
}
