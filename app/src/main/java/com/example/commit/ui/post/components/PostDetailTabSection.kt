package com.example.commit.ui.post.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


enum class TabType {
    DETAIL, ARTIST
}

@Composable
fun PostDetailTabSection(
    modifier: Modifier = Modifier,
    onTabSelected: (TabType) -> Unit
) {
    var selectedTab by remember { mutableStateOf(TabType.DETAIL) }

    Column(modifier = modifier.fillMaxWidth()) {
        // 탭 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            TabType.values().forEach { tab ->
                Column(
                    modifier = Modifier
                        .clickable {
                            selectedTab = tab
                            onTabSelected(tab)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (tab) {
                            TabType.DETAIL -> "상세글"
                            TabType.ARTIST -> "작가정보"
                        },
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedTab == tab) Color.Black else Color(0xFFB0B0B0)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(2.dp)
                            .background(
                                if (selectedTab == tab) Color(0xFF17D5C6)
                                else Color.Transparent
                            )
                    )
                }
            }
        }

        // 탭 내용
        when (selectedTab) {
            TabType.DETAIL -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "당일 내로 작업 가능합니다!\n" +
                                "빠르게 받아보시고 싶은 분들은 편하게 문의 주세요 :)\n\n" +
                                "신청 시에 캐릭터 참고 이미지 첨부해 주시면 되고,\n" +
                                "추가로 원하시는 포즈나 표정 함께 말씀해 주세요!",
                        fontSize = 14.sp,
                        color = Color(0xFF333333)
                    )
                }
            }

            TabType.ARTIST -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "[작가정보 페이지 입니다]", fontSize = 14.sp)
                }
            }
        }
    }
}


@Composable
fun TabItem(title: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.Black else Color(0xFFB0B0B0)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDetailTabSection() {
    PostDetailTabSection(onTabSelected = {})
}
