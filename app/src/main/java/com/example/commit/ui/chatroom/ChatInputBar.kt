package com.example.commit.ui.chatroom



import android.R.attr.query
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.commit.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onPlusClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                keyboardController?.hide()
                onPlusClick()
            },
            modifier = Modifier.size(36.dp) // 36x36 고정
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Add",
                tint = Color.Unspecified
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = message,
            onValueChange = onMessageChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .size(width = 258.dp, height = 36.dp), // Figma 기준 너비/높이
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(20.dp) // Radius 20
                        )
                        .border(
                            width = 1.dp,
                            color = Color(0xFFB0B0B0),       // 연한 회색 테두리
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp) // 텍스트 패딩
                ) {
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                if (message.isNotBlank()) onSendMessage()
            },
            modifier = Modifier.size(36.dp) // 36x36 고정
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_chatsend),
                contentDescription = "Send",
                tint = Color.Unspecified
            )
        }
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

