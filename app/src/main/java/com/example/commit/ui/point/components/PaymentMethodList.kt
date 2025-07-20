package com.example.commit.ui.point.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun PaymentMethodList(
    selectedPayment: String?,
    onSelect: (String) -> Unit
) {
    val paymentMethods = listOf("토스페이", "카카오페이", "신용카드")

    Text("결제 수단", style = CommitTypography.bodyMedium.copy(color = Color.Gray))

    paymentMethods.forEach { method ->
        Row(
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            RadioButton(
                selected = selectedPayment == method,
                onClick = { onSelect(method) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(method, style = CommitTypography.bodyMedium)
        }
    }
}