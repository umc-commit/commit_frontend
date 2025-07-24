package com.example.commit.ui.chatroom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PaymentRequestCard(amount: Int, onPayClick: () -> Unit) {
    Text(text = "결제 요청: ${amount}P")
}
@Preview(showBackground = true)
@Composable
fun PreviewPaymentRequestCard() {
    PaymentRequestCard(amount = 40000, onPayClick = {
        println("결제 버튼 클릭됨")
    })
}
