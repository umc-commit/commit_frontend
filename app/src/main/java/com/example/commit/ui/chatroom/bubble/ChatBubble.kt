package com.example.commit.ui.chatroom.bubble

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType

@Composable
fun ChatBubble(
    message: ChatMessage,
    onPayClick: () -> Unit
) {
    when (message.type) {
        MessageType.TEXT -> TextMessageBubble(message)
        MessageType.COMMISSION_REQUEST -> CommissionRequestBubble(
            requestedAt = message.content,
            onConfirmClick = { /* TODO */ }
        )
        MessageType.COMMISSION_ACCEPTED -> CommissionAcceptedBubble(
            typeName = message.content
        )
        MessageType.PAYMENT -> PaymentRequestBubble(message, onPayClick)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatBubble() {
    Column {
        val currentUserId = "me"

        listOf(
            ChatMessage("1", "me", "안녕하세요", System.currentTimeMillis(), MessageType.TEXT, null),
            ChatMessage("2", "artist", "반가워요!", System.currentTimeMillis(), MessageType.TEXT, null),
            ChatMessage("3", "me", "25.06.02 17:50", System.currentTimeMillis(), MessageType.COMMISSION_REQUEST, null),
            ChatMessage("4", "artist", "낙서 타임 커미션", System.currentTimeMillis(), MessageType.COMMISSION_ACCEPTED, null),
            ChatMessage("5", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 50000),
        ).forEach { msg ->
            MessageWithTimestamp(message = msg, currentUserId = currentUserId, onPayClick = {})
        }
    }
}
