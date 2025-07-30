package com.example.commit.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun FilterButtonRow(
    keyword: String,
    selectedFilters: Set<String> = emptySet(),
    onFilterIconClick: () -> Unit = {},
    onFilterClick: (String) -> Unit = {}
) {
    val filters = listOf(keyword, "최신순", "가격", "당일마감")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 필터 아이콘 버튼
        item {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(1.dp, Color(0xFFE0E0E0), CircleShape)
                    .clickable { onFilterIconClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "필터",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Unspecified
                )
            }
        }

        // 필터 라벨 버튼들
        items(filters, key = { it }) { label ->
            val isSelected = selectedFilters.contains(label)

            val borderColor = if (isSelected) Color(0xFF17D5C6) else Color(0xFFE0E0E0)
            val backgroundColor = if (isSelected) Color(0xFFE0FFFF) else Color.White
            val contentColor = if (isSelected) Color(0xFF17D5C6) else Color(0xFF2B2B2B)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onFilterClick(label) }
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(1.dp, borderColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    style = CommitTypography.bodyMedium.copy(color = contentColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_under_vector),
                    contentDescription = "$label 화살표",
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FilterButtonRowPreview() {
    var selectedFilters by remember { mutableStateOf(setOf("가격", "그림")) }

    FilterButtonRow(
        keyword = "그림",
        selectedFilters = selectedFilters,
        onFilterClick = { label ->
            selectedFilters = if (selectedFilters.contains(label)) {
                selectedFilters - label
            } else {
                selectedFilters + label
            }
        },
        onFilterIconClick = { /* 아이콘 클릭 처리 */ }
    )
}
