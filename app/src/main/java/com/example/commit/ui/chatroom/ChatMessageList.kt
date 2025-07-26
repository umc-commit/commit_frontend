package com.example.commit.ui.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.commit.data.model.ChatMessage
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
        val itemsWithDate = buildList {
            var lastDate: String? = null
            for (message in messages) {
                val currentDate = formatDateHeader(message.timestamp)
                if (lastDate != currentDate) {
                    lastDate = currentDate
                    add("HEADER:$currentDate") // 날짜는 문자열로 구분
                }
                add(message) // 메시지 객체 자체 추가
            }
        }

        items(itemsWithDate) { item ->
            when (item) {
                is ChatMessage -> {
                    MessageWithTimestamp(
                        message = item,
                        currentUserId = currentUserId,
                        onPayClick = onPayClick
                    )
                }

                is String -> {
                    if (item.startsWith("HEADER:")) {
                        val dateText = item.removePrefix("HEADER:")
                        DateHeader(dateText = dateText)
                    }
                }
            }
        }
    }
}


