package com.example.commit.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun SearchInputBar(
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 뒤로가기
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 검색창
        Row(
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3F3F3)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))

            // 검색어
            Text(
                text = "",
                style = CommitTypography.bodyMedium,
                color = Color(0xFF2B2B2B),
                modifier = Modifier.weight(1f)
            )

            // 삭제 아이콘
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "지우기",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
                    .clickable { onClearClick() },
                tint = Color(0xFFA8A8A8)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 홈 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_search_home),
            contentDescription = "홈",
            modifier = Modifier
                .size(24.dp)
                .clickable { onHomeClick() } ,
                    tint = Color(0xFF4D4D4D)
        )
    }
}
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SearchInputBarPreview() {
    SearchInputBar(
        onBackClick = { /* 미리보기용 */ },
        onClearClick = { /* 미리보기용 */ },
        onHomeClick = { /* 미리보기용 */ }
    )
}
