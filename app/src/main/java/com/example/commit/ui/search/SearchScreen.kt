package com.example.commit.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.ui.search.components.*

@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onTotalClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val recommendedTags = listOf("SD", "LD", "반려동물", "커플", "오마카세")
    val recentSearches = remember {
        mutableStateListOf(
            "고양이", "예시 텍스트", "가나다라마바사", "랜덤 커미션"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        // 🔍 검색창
        SearchBarWithBack(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            onSearchClick = { /* 검색 기능 구현 */ },
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 카테고리
        CategoryRow(
            onTotalClick = onTotalClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 🏷 추천 태그
        RecommendedTagsSection(tags = recommendedTags)

        Spacer(modifier = Modifier.height(24.dp))

        // 구분선
        Divider(
            color = Color(0xFFF3F3F3),
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 최근 검색
        RecentSearchSection(
            searches = recentSearches,
            onDeleteItem = { index -> recentSearches.removeAt(index) },
            onClearAll = { recentSearches.clear() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}
