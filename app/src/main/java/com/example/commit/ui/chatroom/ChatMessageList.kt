package com.example.commit.ui.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    onFormCheckClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 메시지 개수 제한으로 메모리 사용량 최적화
    val limitedMessages = remember(messages) {
        messages.takeLast(50) // 최근 50개 메시지만 표시
    }

    LaunchedEffect(limitedMessages.size) {
        if (limitedMessages.isNotEmpty()) {
            try {
                listState.scrollToItem(limitedMessages.size - 1)
            } catch (e: Exception) {
                // 스크롤 실패 시 안전하게 처리
            }
        }
    }
    
    LazyColumn(state = listState,modifier = modifier.background(color = Color(0xFFF5F5F5))
        .imePadding()) {
        val itemsWithDate = buildList<Any> {
            var lastDate: String? = null
            for (message in limitedMessages) {
                val currentDate = formatDateHeader(message.timestamp)
                if (lastDate != currentDate) {
                    lastDate = currentDate
                    add("HEADER:$currentDate")
                }
                add(message)
            }
        }

        var lastMessage: ChatMessage? = null

        itemsIndexed(itemsWithDate) { index, item ->
            when (item) {
                is ChatMessage -> {
                    val prev = lastMessage

                    val topPadding = when {
                        prev == null -> 29.dp
                        prev.senderId != item.senderId -> 29.dp
                        isSystem(prev) && isSystem(item) &&
                                prev.senderId == item.senderId -> 19.dp

                        !isSystem(prev) && !isSystem(item) &&
                                prev.senderId == item.senderId -> 10.dp

                        else -> 19.dp
                    }

                    Spacer(modifier = Modifier.height(topPadding))

                    MessageWithTimestamp(
                        message = item,
                        currentUserId = currentUserId,
                        onPayClick = onPayClick,
                        onFormCheckClick = onFormCheckClick
                    )

                    lastMessage = item

                    // ✅ 마지막 메시지라면 33dp Spacer 추가
                    if (index == itemsWithDate.lastIndex) {
                        Spacer(modifier = Modifier.height(33.dp))
                    }
                }

                is String -> {
                    if (item.startsWith("HEADER:")) {
                        val dateText = item.removePrefix("HEADER:")
                        DateHeader(dateText = dateText)
                        lastMessage = null
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



