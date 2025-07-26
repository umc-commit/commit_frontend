package com.example.commit.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType

class ChatViewModel : ViewModel() {

    val currentUserId = "me"

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

        val newChat = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = currentUserId,
            content = message,
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            amount = null
        )

        chatMessages = chatMessages + newChat
        println("보낸 메시지: $message")
        message = ""
    }

    init {
        chatMessages = listOf(
            ChatMessage("1", "me", "안녕하세요", System.currentTimeMillis(), MessageType.TEXT, null),
            ChatMessage("2", "artist", "반가워요!", System.currentTimeMillis(), MessageType.TEXT, null),
            ChatMessage("3", "me", "25.06.02 17:50", System.currentTimeMillis(), MessageType.COMMISSION_REQUEST, null),
            ChatMessage("4", "artist", "낙서 타임 커미션", System.currentTimeMillis(), MessageType.COMMISSION_ACCEPTED, null),
            ChatMessage("5", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 50000)
        )
    }

}
