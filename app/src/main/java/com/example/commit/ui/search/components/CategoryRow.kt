package com.example.commit.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun CategoryRow(
    onCategoryClick: (String) -> Unit = {},
    onTotalClick: () -> Unit = {}
) {
    val scrollableCategories = listOf(
        R.drawable.ic_text to "글",
        R.drawable.ic_drawing to "그림",
        R.drawable.ic_video to "영상",
        R.drawable.ic_design to "디자인",
        R.drawable.ic_goods to "굿즈",
        R.drawable.ic_fortune to "점술",
        R.drawable.ic_sound to "사운드",
        R.drawable.ic_motion to "모션",
        R.drawable.ic_outsource to "외주",
        R.drawable.ic_temp to "기타"
    )
    val fixedCategory = "전체" to R.drawable.ic_total

    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 스크롤 가능한 카테고리
            LazyRow(
                state = rememberLazyListState(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 44.dp, end = 48.dp)
            ) {
                items(scrollableCategories) { (iconRes, label) ->
                    CategoryItem(
                        iconRes = iconRes,
                        label = label,
                        onClick = { onCategoryClick(label) }
                    )
                }
            }

            // 고정된 "전체" 카테고리
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(48.dp)
                    .offset(x = (-35).dp)
                    .clickable { onTotalClick() }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF3F3F3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = fixedCategory.second),
                        contentDescription = fixedCategory.first,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = fixedCategory.first,
                    style = CommitTypography.bodyMedium,
                    color = Color.Black
                )
            }
        }

        // 오른쪽 오버레이
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(16.dp)
                .height(64.dp)
                .background(Color.White)
        )
    }
}

@Composable
private fun CategoryItem(
    label: String,
    iconRes: Int,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F3F3)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = CommitTypography.bodyMedium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CategoryRowPreview() {
    CategoryRow(
        onCategoryClick = { label -> println("카테고리 선택됨: $label") },
        onTotalClick = { println("전체 클릭됨") }
    )
}
