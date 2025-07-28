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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.ui.Theme.notoSansKR

@Composable
fun TextMessageBubble(
    message: ChatMessage,
    currentUserId: String = "me",
    modifier: Modifier = Modifier
) {
    val isMe = message.senderId == currentUserId
    val cleanLength = message.content.replace(" ", "").length
    val chunkedText = if (cleanLength > 20) {
        message.content.chunked(20).joinToString("\n")
    } else {
        message.content
    }

    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    topStart = if (isMe) 8.dp else 0.dp,
                    topEnd = if (isMe) 0.dp else 8.dp,
                    bottomEnd = 8.dp,
                    bottomStart = 8.dp
                )
            )
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 11.dp)
            .wrapContentWidth(unbounded = true)
    ) {
        Text(
            text = chunkedText,
            style = TextStyle(
                fontFamily = notoSansKR,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                letterSpacing = 0.sp
            ),
            color = Color.Black
        )


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
