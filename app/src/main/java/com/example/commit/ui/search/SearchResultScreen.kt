package com.example.commit.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.search.components.FilterButtonRow
import com.example.commit.ui.search.components.SearchInputBar

@Composable
fun SearchResultScreen(
    keyword: String,
    resultCount: Int = 1275,
    selectedFilters: Set<String> = emptySet(),
    showFollowOnly: Boolean = false,
    onFollowToggle: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFilterIconClick: () -> Unit = {},
    onFilterClick: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 16.dp)
    ) {
        // 검색창
        SearchInputBar(
            onBackClick = onBackClick,
            onClearClick = onClearClick,
            onHomeClick = onHomeClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 필터 버튼들
        FilterButtonRow(
            keyword = keyword,
            selectedFilters = selectedFilters,
            onFilterIconClick = onFilterIconClick,
            onFilterClick = onFilterClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 결과 수 + 팔로우 토글
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${resultCount}건",
                color = Color(0xFF17D5C6),
                fontSize = 14.sp
            )
            Text(
                text = "의 커미션이 검색됐어요.",
                color = Color(0xFF2B2B2B),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            FollowOnlyToggle(
                isChecked = showFollowOnly,
                onToggle = { onFollowToggle(!showFollowOnly) }
            )
        }
    }
}

@Composable
fun FollowOnlyToggle(
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onToggle() }
    ) {
        Text(
            text = "팔로우 중인 작가만",
            color = Color(0xFF4D4D4D),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(
                    width = 2.dp,
                    color = Color(0xFF17D5C6),
                    shape = CircleShape
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF17D5C6), shape = CircleShape)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchResultScreenPreview() {
    var selectedFilters by remember { mutableStateOf(setOf("커미션", "가격")) }
    var checked by remember { mutableStateOf(true) }

    SearchResultScreen(
        keyword = "커미션",
        selectedFilters = selectedFilters,
        onFilterClick = { label ->
            selectedFilters = if (selectedFilters.contains(label)) {
                selectedFilters - label
            } else {
                selectedFilters + label
            }
        },
        showFollowOnly = checked,
        onFollowToggle = { checked = it }
    )
}
