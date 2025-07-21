package com.example.commit.ui.chatroom


import android.R.attr.contentDescription
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.material3.TextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.example.commit.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onPlusClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 20.dp), // 바깥 여백
        verticalAlignment = Alignment.CenterVertically
    ) {
        // + 버튼
        IconButton(
            onClick = onPlusClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Add",
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // 텍스트 입력 영역
        OutlinedTextField(
            value = message,
            onValueChange = onMessageChange,
            singleLine = true,
            placeholder = { Text(" ", color = Color(0xFFB0B0B0)) },
            modifier = Modifier
                .weight(1f)
                .height(36.dp), // Figma 기준
            shape = RoundedCornerShape(20.dp), // radius 20
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color(0xFFB0B0B0),
                unfocusedBorderColor = Color(0xFFB0B0B0),
                cursorColor = Color(0xFFB0B0B0)
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 전송 버튼
        IconButton(
            onClick = {
                if (message.isNotBlank()) onSendMessage()
            },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chatsend),
                contentDescription = "Send",
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun ChatBottomSection(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isMenuOpen: Boolean,
    onToggleMenu: () -> Unit
) {
    Column {
        if (isMenuOpen) {
            FileOptionMenu() // 앨범/카메라/파일 아이콘 UI
        }

        ChatInputBar(
            message = message,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            onPlusClick = onToggleMenu
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewChatInputBar() {
    var message by remember { mutableStateOf("") }

    ChatInputBar(
        message = message,
        onMessageChange = { message = it },
        onSendMessage = {
            println("보낸 메시지: $message")
            message = ""
        },
        onPlusClick = {
            println("+ 버튼 눌림")
        }
    )
}

