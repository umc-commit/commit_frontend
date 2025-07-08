package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.request.notoSansKR

@Composable
fun FilterRow(selected: String, onSelect: (String) -> Unit) {
    val filters = listOf("전체", "진행 중", "작업 완료")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { label ->
                val isSelected = selected == label
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = if (isSelected) Color(0x1A17D5C6) else Color.White // mint1 10% 투명도
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected)
                                Color(0xFF17D5C6) // mint1
                            else
                                Color(0xFFB0B0B0), // gray2
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(label) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontFamily = notoSansKR,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected)
                            Color(0xFF17D5C6) // mint1
                        else
                            Color(0xFF909090)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "정렬",
                fontSize = 14.sp,
                fontFamily = notoSansKR,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF4D4D4D) // black2
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_under_vector),
                contentDescription = "정렬 화살표",
                tint = Color(0xFF4D4D4D), // black2
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterRowPreview() {
    var selected by remember { mutableStateOf("전체") }

    FilterRow(selected = selected, onSelect = { selected = it })
}
