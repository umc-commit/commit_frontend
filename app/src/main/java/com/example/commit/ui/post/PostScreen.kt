package com.example.commit.ui.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.ui.post.components.PostBottomBar
import com.example.commit.ui.post.components.PostHeaderSection
import androidx.compose.foundation.layout.navigationBarsPadding


@Composable
fun PostScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        // 본문 - 스크롤 가능 영역
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp) // 하단 바 영역 고려한 패딩
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
            onChatClick = { /* 채팅 클릭 처리 */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

    }
}

@Preview(showBackground = true, name = "Post Screen Preview")
@Composable
fun PreviewPostScreen() {
    PostScreen()
}