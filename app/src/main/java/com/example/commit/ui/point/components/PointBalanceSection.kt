package com.example.commit.ui.point.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun PointBalanceSection(currentPoint: Int) {
    Text("보유 포인트", style = CommitTypography.bodyMedium.copy(color = Color.Gray))
    Text("${"%,d".format(currentPoint)}P", style = CommitTypography.headlineSmall)
}