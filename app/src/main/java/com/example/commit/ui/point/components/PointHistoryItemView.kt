package com.example.commit.ui.point.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.point.PointHistoryItem

@Composable
fun PointHistoryItemView(item: PointHistoryItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // 날짜
        val formattedDate = "25.06.04 13:05"

        Text(
            text = formattedDate,
            style = CommitTypography.labelSmall.copy(color = Color.Gray)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "포인트 ${item.status}",
                    style = CommitTypography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                )
                Text(
                    text = "카카오페이 결제",
                    style = CommitTypography.labelSmall.copy(color = Color.Gray)
                )
            }

            val amountText = String.format("%,dP", kotlin.math.abs(item.amount))
            val amountColor = if (item.amount >= 0) Color(0xFF17D5C6) else Color.Red

            Text(
                text = if (item.amount >= 0) amountText else "-$amountText",
                style = CommitTypography.bodyMedium.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = amountColor
                )
            )
        }
    }
}