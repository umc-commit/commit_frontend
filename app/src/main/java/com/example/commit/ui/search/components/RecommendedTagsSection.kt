package com.example.commit.ui.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
fun RecommendedTagsSection(tags: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "추천 태그",
            style = TextStyle(
                fontFamily = notoSansKR,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            ),
            color = Color(0xFF333333),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp) // 칩 높이에 맞게 고정 (줄바꿈 방지)
        ) {
            items(tags) { tag ->
                TagChip(tag)
            }
        }
    }
}

@Composable
fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .border(
                width = 0.5.dp,
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "#$tag",
            style = TextStyle(
                fontFamily = notoSansKR,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = Color(0xFF4D4D4D)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RecommendedTagsSectionPreview() {
    val sampleTags = listOf(
        "SD", "LD", "반려동물", "커플", "오마카세", "풍경",
        "공포", "캐릭터", "데포르메", "아기", "게임", "패션"
    )
    RecommendedTagsSection(tags = sampleTags)
}
