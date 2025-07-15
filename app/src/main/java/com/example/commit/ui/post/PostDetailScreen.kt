package com.example.commit.ui.post.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R

@Composable
fun PostHeaderSection(
    title: String,
    tags: List<String>,
    minPrice: Int,
    summary: String,
    imageCount: Int = 3,
    currentIndex: Int = 0
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // 상단 앱바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
        ) {
            // 왼쪽 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_left_vector),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
            )

            // 가운데 제목
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 이미지 Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFE0E0E0))
        )

        // 이미지 인디케이터
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center


        ) {
            repeat(imageCount) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .size(8.dp)
                        .background(
                            if (index == currentIndex) Color.Black else Color.LightGray,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

        // 게시글 정보 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 제목 + 북마크
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_unselect_bookmarket),
                    contentDescription = "북마크",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 태그 뱃지
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tags.forEachIndexed { index, tag ->
                    val isCategory = index == 0
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .wrapContentWidth()
                            .background(
                                color = if (isCategory) Color(0xFFE6FFFB) else Color(0xFFF2F2F2),
                                shape = RoundedCornerShape(50)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCategory) Color(0xFF17D5C6) else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tag,
                            fontSize = 12.sp,
                            color = if (isCategory) Color(0xFF17D5C6) else Color(0xFFB0B0B0),
                            textDecoration = if (isCategory) TextDecoration.Underline else null
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 가격
            Text(
                text = String.format("%,dP~", minPrice),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 설명
            Text(
                text = summary,
                fontSize = 14.sp,
                color = Color(0xFF333333)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostHeaderSection() {
    PostHeaderSection(
        title = "낙서 타입 커미션",
        tags = listOf("그림", "#LD", "#당일마감"),
        minPrice = 10000,
        summary = "최선을 다해 그려드려요. 잘 부탁드립니다!"
    )
}
