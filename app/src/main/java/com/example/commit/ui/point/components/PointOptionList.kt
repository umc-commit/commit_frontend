package com.example.commit.ui.point.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun PointOptionList(
    selectedPoint: Int?,
    onSelect: (Int) -> Unit
) {
    val pointOptions = listOf(
        1000 to 1000,
        3000 to 3200,
        5000 to 5300,
        10000 to 10500,
        50000 to 51000
    )

    Text("포인트 충전", style = CommitTypography.bodyMedium.copy(color = Color.Gray))

    Column {
        pointOptions.forEach { (point, price) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .border(
                        width = 1.dp,
                        color = if (selectedPoint == point) Color(0xFF17D5C6) else Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onSelect(point) }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$point P", style = CommitTypography.bodyMedium)
                Text("₩${"%,d".format(price)}", style = CommitTypography.bodyMedium)
            }
        }
    }
}