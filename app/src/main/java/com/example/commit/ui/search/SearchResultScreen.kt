package com.example.commit.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.request.components.Commission
import com.example.commit.ui.request.components.CommissionCard
import com.example.commit.ui.search.components.*
import com.example.commit.ui.search.components.FilterBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    keyword: String,
    resultCount: Int = 1275,
    commissions: List<Commission>,
    selectedFilters: Set<String> = emptySet(),
    showFollowOnly: Boolean = false,
    onFollowToggle: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    onClearClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onFilterClick: (String) -> Unit = {},
    onCommissionClick: (Commission) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedSort by remember { mutableStateOf("최신순") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var selectedDeadline by remember { mutableStateOf("전체보기") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            state = rememberLazyGridState()
        ) {
            item(span = { GridItemSpan(2) }) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))

                    SearchInputBar(
                        query = query,
                        onQueryChange = { query = it },
                        onBackClick = onBackClick,
                        onClearClick = { query = "" },
                        onHomeClick = onHomeClick
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FilterButtonRow(
                        keyword = keyword,
                        selectedFilters = selectedFilters,
                        onFilterClick = onFilterClick,
                        onFilterIconClick = { showBottomSheet = true }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.offset(y = 15.dp)
                        ) {
                            Text(
                                text = "11건",
                                color = Color(0xFF17D5C6),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "의 커미션이 검색되었어요.",
                                color = Color(0xFF2B2B2B),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier.offset(y = (-4).dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FollowOnlyToggle(
                                isChecked = showFollowOnly,
                                onToggle = { onFollowToggle(!showFollowOnly) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            items(commissions) { commission ->
                CommissionCard(
                    commission = commission,
                    modifier = Modifier.clickable {
                        onCommissionClick(commission) // 👉 외부 콜백 호출
                    }
                )
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            FilterBottomSheet(
                onDismiss = { showBottomSheet = false },
                selectedSort = selectedSort,
                onSortChange = { selectedSort = it },
                minPrice = minPrice,
                maxPrice = maxPrice,
                onMinPriceChange = { minPrice = it },
                onMaxPriceChange = { maxPrice = it },
                selectedDeadline = selectedDeadline,
                onDeadlineChange = { selectedDeadline = it },
                onReset = {
                    selectedSort = "최신순"
                    minPrice = ""
                    maxPrice = ""
                    selectedDeadline = "전체보기"
                },
                onApply = {
                    showBottomSheet = false
                    // TODO: 필터 적용
                }
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
                ),
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
