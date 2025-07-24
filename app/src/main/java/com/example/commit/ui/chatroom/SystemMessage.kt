package com.example.commit.ui.chatroom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType

@Composable
fun SystemMessage(msg: ChatMessage) {
    Text(text = "[SYSTEM] ${msg.message}")
}
@Preview(showBackground = true)
@Composable
fun PreviewSystemMessage() {
    val message = ChatMessage(
        id = "2",
        senderId = "system",
        message = "커미션 신청이 접수되었습니다.",
        timestamp = System.currentTimeMillis(),
        type = MessageType.SYSTEM,
        amount = null
    )
    SystemMessage(msg = message)
}
