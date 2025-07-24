package com.example.commit.ui.point.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun ChargeButton(enabled: Boolean) {
    Button(
        onClick = { /* TODO: 결제 처리 */ },
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (enabled) Color.Black else Color.LightGray,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "결제하기",
            style = CommitTypography.bodyMedium.copy(color = Color.White)
        )
    }
}
