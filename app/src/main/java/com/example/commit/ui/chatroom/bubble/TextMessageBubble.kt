package com.example.commit.ui.chatroom.bubble

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType

@Composable
fun TextMessageBubble(message: ChatMessage, currentUserId: String = "me") {
    val isMe = message.senderId == currentUserId

    // 1. 공백 제거 후 20자 초과 시 수동 줄바꿈
    val cleanLength = message.content.replace(" ", "").length
    val chunkedText = if (cleanLength > 20) {
        message.content.chunked(20).joinToString("\n")
    } else {
        message.content
    }

    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(horizontal = 14.dp, vertical = 11.dp)
                .wrapContentWidth() // 너무 길어지지 않도록 제한
        ) {
            Text(
                text = chunkedText,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = Color.Black,
                softWrap = true,         // 자동 줄바꿈 허용
                maxLines = Int.MAX_VALUE // 줄 수 제한 없음
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewTextMessageBubble() {
    Column {
        TextMessageBubble(
            message = ChatMessage(
                id = "1",
                senderId = "me",
                content = "내가 보낸 메시지",
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT,
                amount = null
            )
        )
        TextMessageBubble(
            message = ChatMessage(
                id = "2",
                senderId = "artist",
                content = "상대방이 보낸 메시지",
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT,
                amount = null
            )
        )
    }
}
