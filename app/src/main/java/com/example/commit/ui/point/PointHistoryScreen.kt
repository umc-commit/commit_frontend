package com.example.commit.ui.point

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.point.components.PointHistoryItemView

data class PointHistoryItem(
    val transactionId: Int,
    val status: String,
    val amount: Int,
    val created_at: String
)
@Composable
fun PointHistoryScreen(
    pointList: List<PointHistoryItem>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        // 상단 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_left_vector),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Text(
                text = "충전 내역",
                style = CommitTypography.headlineSmall
            )
        }

        // 내역 목록
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(pointList) { item ->
                PointHistoryItemView(item = item)
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            }
        }
    }
}
