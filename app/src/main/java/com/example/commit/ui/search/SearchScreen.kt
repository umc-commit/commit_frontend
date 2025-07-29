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
import com.example.commit.ui.request.components.Commission

@Composable
fun SearchScreen(
    onBackClick: () -> Unit = {},
    onTotalClick: () -> Unit = {} // ✅ 전체 클릭 시 전달받는 람다
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedKeyword by remember { mutableStateOf("") }

    val dummyCommissions = List(10) {
        Commission(
            nickname = "작가$it",
            title = "테스트 커미션 $it",
            tags = listOf("그림", "#예시", "#귀엽음")
        )
    }

    val recommendedTags = listOf("SD", "LD", "반려동물", "커플", "오마카세")
    val recentSearches = remember {
        mutableStateListOf("고양이", "예시 텍스트", "가나다라마바사", "랜덤 커미션")
    }

    if (selectedKeyword.isNotEmpty()) {
        SearchResultScreen(
            keyword = selectedKeyword,
            commissions = dummyCommissions,
            selectedFilters = emptySet(),
            showFollowOnly = false,
            onBackClick = { selectedKeyword = "" },
            onClearClick = { searchQuery = "" },
            onHomeClick = {},
            onFilterClick = {},
            onFollowToggle = {}
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
        ) {
            SearchBarWithBack(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearchClick = {},
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            CategoryRow(
                onCategoryClick = { label -> selectedKeyword = label }, // ✅ 개별 아이콘 클릭 시
                onTotalClick = onTotalClick // ✅ 전체 아이콘 클릭 시 → 외부로 위임
            )

            Spacer(modifier = Modifier.height(24.dp))

            RecommendedTagsSection(tags = recommendedTags)

            Spacer(modifier = Modifier.height(24.dp))

            Divider(
                color = Color(0xFFF3F3F3),
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            RecentSearchSection(
                searches = recentSearches,
                onDeleteItem = { index -> recentSearches.removeAt(index) },
                onClearAll = { recentSearches.clear() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen()
}
