package com.example.commit.ui.chatroom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType

@Composable
fun ChatTextMessage(msg: ChatMessage) {
    Text(text = msg.message)
}
@Preview(showBackground = true)
@Composable
fun PreviewChatTextMessage() {
    val message = ChatMessage(
        id = "1",
        senderId = "user123",
        message = "안녕하세요 작가님!",
        timestamp = System.currentTimeMillis(),
        type = MessageType.TEXT,
        amount = null
    )
    ChatTextMessage(msg = message)
}
