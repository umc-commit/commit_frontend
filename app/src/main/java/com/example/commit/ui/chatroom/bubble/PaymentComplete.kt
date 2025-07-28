package com.example.commit.ui.chatroom.bubble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun PaymentComplete() {
    Column(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(
                topStart = 8.dp,
                bottomEnd = 8.dp,
                bottomStart = 8.dp
            ))
            .padding(16.dp)
            .width(200.dp)
    ) {
        Text(text = "결제 완료", style = CommitTypography.headlineSmall.copy(fontSize = 12.sp), color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "결제가 완료됐어요. 24시간 이내로\n" +
                "작업을 시작해주세요.", fontSize = 10.sp, color = Color.Black)
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewPaymentComplete() {
    PaymentComplete()
}
