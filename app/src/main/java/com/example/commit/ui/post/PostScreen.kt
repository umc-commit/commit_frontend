package com.example.commit.ui.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.post.components.PostBottomBar
import com.example.commit.ui.post.components.PostHeaderSection
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.zIndex


@Composable
fun PostScreen() {
    var isSheetVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // 본문
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            PostHeaderSection(
                title = "그림 커미션",
                tags = listOf("그림", "#LD", "#당일마감"),
                minPrice = 10000,
                summary = "빠르게 작업해드립니다!"
            )
        }

        // 하단 고정 바
        PostBottomBar(
            isRecruiting = true,
            remainingSlots = 11,
            onApplyClick = { /* 신청 클릭 처리 */ },
            onChatClick = { isSheetVisible = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        // 하단 시트 모달
        if (isSheetVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000))
                    .clickable { isSheetVisible = false }
                    .zIndex(1f)
            )

            // 바텀시트 내용
            AnimatedVisibility(
                visible = isSheetVisible,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .align(Alignment.BottomCenter)
                    .zIndex(2f)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    elevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "카카오톡으로 공유하기",
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "신고하기",
                            fontSize = 16.sp,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostScreen() {
    PostScreen()
}
