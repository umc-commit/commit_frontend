package com.example.commit.ui.chatroom.bubble

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun CommissionRequestBubble(
    requestedAt: String,
    onConfirmClick: () -> Unit,
    onFormCheckClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(
                topStart = 8.dp,
                bottomEnd = 8.dp,
                bottomStart = 8.dp
            ))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .width(200.dp)
    ) {
        Text(
            text = "커미션 신청",
            style = CommitTypography.headlineSmall.copy(fontSize = 12.sp),
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(9.dp))
        Text(
            text = "신청 시간 : $requestedAt",
            fontSize = 10.sp,
            color = Color(0xFF4D4D4D)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Divider(
            color = Color(0xFFF1F1F1),
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onFormCheckClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(0.5.dp, Color(0xFF4D4D4D))
        ) {
            Text(
                text = "신청서 확인하기",
                style = CommitTypography.bodyMedium.copy(fontSize = 12.sp,color = Color(0xFF4D4D4D))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommissionRequestBubble() {
    CommissionRequestBubble(
        requestedAt = "25.06.02 17:50",
        onConfirmClick = { println("신청서 확인하기 클릭됨") },
        onFormCheckClick = { println("신청서 확인하기 클릭됨") }
    )
}
