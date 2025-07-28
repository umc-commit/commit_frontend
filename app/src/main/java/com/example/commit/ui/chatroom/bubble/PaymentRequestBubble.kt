package com.example.commit.ui.chatroom.bubble

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.ui.Theme.CommitTypography
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun PaymentRequestBubble(message: ChatMessage, onPayClick: () -> Unit) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(
                topEnd = 8.dp,
                bottomEnd = 8.dp,
                bottomStart = 8.dp
            ))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .width(200.dp)
    ) {
        Text(text = "결제 요청", style = CommitTypography.headlineSmall.copy(fontSize = 12.sp),
            color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "${String.format("%,d", message.amount)} P", style = CommitTypography.bodyMedium,
            color = Color(0xFF17D5C6))

        Spacer(modifier = Modifier.height(10.dp))
        Divider(
            color = Color(0xFFF1F1F1),
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(0.5.dp, Color(0xFF4D4D4D))
        ) {
            Text(
                text = "결제하기",
                style = CommitTypography.labelSmall.copy(fontWeight = FontWeight.SemiBold,color = Color(0xFF4D4D4D))
            )
        }
        if (showDialog) {
            PaymentConfirmDialog(
                amount = message.amount ?: 0,
                onConfirm = {
                    // 여기에 결제 API 호출 등 넣기
                    Toast.makeText(context, "결제가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaymentRequestBubble() {
    PaymentRequestBubble(
        message = ChatMessage(
            id = "4",
            senderId = "artist",
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.PAYMENT,
            amount = 40000
        ),
        onPayClick = { println("결제 버튼 클릭됨") }
    )
}

