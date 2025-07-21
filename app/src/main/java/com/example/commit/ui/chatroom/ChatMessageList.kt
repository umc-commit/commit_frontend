package com.example.commit.ui.chatroom

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.commit.data.model.ChatMessage
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.data.model.MessageType


@Composable
fun ChatMessageList(messages: List<ChatMessage>, onPayClick: () -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight(1f)
            .padding(horizontal = 12.dp)
    ) {
        items(messages) { msg ->
            when (msg.type) {
                MessageType.TEXT -> ChatTextMessage(msg)
                MessageType.SYSTEM -> SystemMessage(msg)
                MessageType.PAYMENT -> PaymentRequestCard(msg.amount ?: 0, onPayClick)
            }


        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewChatMessageList() {
    val messages = listOf(
        ChatMessage("1", "user1", "신청서를 보냈습니다", System.currentTimeMillis(), MessageType.SYSTEM, null),
        ChatMessage("2", "user2", "확인했습니다", System.currentTimeMillis(), MessageType.TEXT, null),
        ChatMessage("3", "user2", "", System.currentTimeMillis(), MessageType.PAYMENT, 40000)
    )

    ChatMessageList(messages = messages, onPayClick = { println("결제하기 클릭") })
}

