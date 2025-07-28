package com.example.commit.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CategoryScreen(onBackClicked: () -> Unit) {
    val categories = listOf(
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // 헤더
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(100.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onBackClicked() }
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "전체 카테고리",
                    style = CommitTypography.headlineSmall,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(modifier = Modifier.size(24.dp)) {}
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 카테고리 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { (iconRes, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3F3F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = label,
                        style = CommitTypography.bodyMedium,
                        color = Color(0xFF2B2B2B)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CategoryScreenPreview() {
    CategoryScreen(onBackClicked = {})
}
