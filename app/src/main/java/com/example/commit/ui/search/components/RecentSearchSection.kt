package com.example.commit.ui.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.notoSansKR

@Composable
fun RecentSearchSection(
    searches: List<String>,
    onDeleteItem: (Int) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "최근 검색",
                style = TextStyle(
                    fontFamily = notoSansKR,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = Color(0xFF333333)
            )

            Text(
                text = "전체 삭제",
                style = TextStyle(
                    fontFamily = notoSansKR,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp
                ),
                color = Color(0xFFB0B0B0),
                modifier = Modifier.clickable { onClearAll() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(searches.take(10)) { index, item ->
                RecentSearchChip(
                    text = item,
                    onDelete = { onDeleteItem(index) }
                )
            }
        }
    }
}

@Composable
fun RecentSearchChip(
    text: String,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = notoSansKR,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = Color(0xFF4D4D4D)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "삭제",
            tint = Color(0xFFB0B0B0),
            modifier = Modifier
                .size(14.dp)
                .clickable { onDelete() }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RecentSearchSectionPreview() {
    val dummySearches = listOf(
        "고양이", "예시 텍스트", "가나다라마바사", "랜덤 커미션", "SD", "커플", "디자인", "일러스트", "게임", "강아지", "추가 검색어"
    )
    RecentSearchSection(
        searches = dummySearches,
        onDeleteItem = {},
        onClearAll = {}
    )
}
