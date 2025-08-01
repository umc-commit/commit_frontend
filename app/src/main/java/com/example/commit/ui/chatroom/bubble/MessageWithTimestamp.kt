package com.example.commit.ui.chatroom.bubble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.util.formatTime

@Composable
fun MessageWithTimestamp(
    message: ChatMessage,
    currentUserId: String,
    onPayClick: () -> Unit,
    onFormCheckClick: () -> Unit
) {
    val isMe = message.senderId == currentUserId

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 27.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom // 말풍선과 시간 아래 정렬
    ) {
        if (!isMe) {
            ChatBubble(
                message = message,
                onPayClick = onPayClick,
                onFormCheckClick = onFormCheckClick,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatTime(message.timestamp),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Bottom)
            )
        } else {
            Text(
                text = formatTime(message.timestamp),
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Bottom)
            )
            Spacer(modifier = Modifier.width(4.dp))
            ChatBubble(
                message = message,
                onPayClick = onPayClick,
                onFormCheckClick = onFormCheckClick,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewMessageWithTimestamp() {
    Column {
        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg0",
                senderId = "me",
                content = "안녕하세요",
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )

        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg00",
                senderId = "artist",
                content = "반가워요!",
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )

        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg1",
                senderId = "me",
                content = "25.06.02 17:50",
                timestamp = System.currentTimeMillis(),
                type = MessageType.COMMISSION_REQUEST,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )

        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg2",
                senderId = "artist",
                content = "낙서 타임 커미션",
                timestamp = System.currentTimeMillis(),
                type = MessageType.COMMISSION_ACCEPTED,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )

        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg3",
                senderId = "artist",
                content = "",
                timestamp = System.currentTimeMillis(),
                type = MessageType.PAYMENT,
                amount = 40000
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )

        // 추가된 메시지들
        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg6",
                senderId = "me",
                content = "",
                timestamp = System.currentTimeMillis(),
                type = MessageType.PAYMENT_COMPLETE,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )

        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg7",
                senderId = "artist",
                content = "",
                timestamp = System.currentTimeMillis(),
                type = MessageType.COMMISSION_START,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )
        MessageWithTimestamp(
            message = ChatMessage(
                id = "msg8",
                senderId = "artist",
                content = "25.06.02 17:50",
                timestamp = System.currentTimeMillis(),
                type = MessageType.COMMISSION_COMPLETE,
                amount = null
            ),
            currentUserId = "me",
            onPayClick = {},
            onFormCheckClick = {}
        )
    }
}



