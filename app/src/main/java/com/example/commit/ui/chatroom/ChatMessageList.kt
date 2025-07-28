package com.example.commit.ui.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.ui.chatroom.bubble.DateHeader
import com.example.commit.ui.chatroom.bubble.MessageWithTimestamp
import com.example.commit.util.formatDateHeader

@Composable
fun ChatMessageList(
    messages: List<ChatMessage>,
    currentUserId: String,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.background(color = Color(0xFFF5F5F5))) {
        val itemsWithDate = buildList<Any> {
            var lastDate: String? = null
            for (message in messages) {
                val currentDate = formatDateHeader(message.timestamp)
                if (lastDate != currentDate) {
                    lastDate = currentDate
                    add("HEADER:$currentDate")
                }
                add(message)
            }
        }

        var lastMessage: ChatMessage? = null

        items(itemsWithDate) { item ->
            when (item) {
                is ChatMessage -> {
                    // 패딩 계산
                    val topPadding = when {
                        lastMessage == null -> 0.dp
                        isSystem(lastMessage as ChatMessage) && isSystem(item) -> 15.dp
                        isSystem(lastMessage as ChatMessage) xor isSystem(item) -> 10.dp
                        else -> 5.dp
                    }


                    Spacer(modifier = Modifier.height(topPadding))

                    MessageWithTimestamp(
                        message = item,
                        currentUserId = currentUserId,
                        onPayClick = onPayClick
                    )

                    lastMessage = item
                    Spacer(modifier = Modifier.height(33.dp))
                }

                is String -> {
                    if (item.startsWith("HEADER:")) {
                        val dateText = item.removePrefix("HEADER:")
                        DateHeader(dateText = dateText)
                        lastMessage = null // 헤더가 들어가면 다음 메시지가 첫 메시지 취급
                    }
                }
            }
        }
    }
}
private fun isSystem(message: ChatMessage): Boolean {
    return message.type == MessageType.PAYMENT ||
            message.type == MessageType.COMMISSION_REQUEST ||
            message.type == MessageType.COMMISSION_ACCEPTED ||
            message.type == MessageType.PAYMENT_COMPLETE ||
            message.type == MessageType.COMMISSION_START ||
            message.type == MessageType.COMMISSION_COMPLETE
}



