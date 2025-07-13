package com.example.commit.ui.request.form

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun CommissionFormScreen() {
    // 스크롤 상태
    val scrollState = rememberScrollState()

    // 기본 상태
    val images = remember { mutableStateListOf<Bitmap>() }
    var text by remember { mutableStateOf("") }

    // 질문 상태
    var answer1 by remember { mutableStateOf("") }
    var answer2 by remember { mutableStateOf("") }
    var answer3 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .width(400.dp)
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState)
    ) {
        // 0. TopBar
        CommissionTopBar()

        // 1. Header
        CommissionHeader()

        Spacer(modifier = Modifier.height(20.dp))

        // 2. 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFD9D9D9))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 3. 질문 섹션들
        CommissionOptionSection(
            index = 1,
            title = "당일마감 옵션",
            options = listOf("O (+10000P)", "X"),
            selectedOption = answer1,
            onOptionSelected = { answer1 = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CommissionOptionSection(
            index = 2,
            title = "신청 캐릭터",
            options = listOf("고양이", "햄스터", "캐리커쳐", "랜덤"),
            selectedOption = answer2,
            onOptionSelected = { answer2 = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CommissionOptionSection(
            index = 3,
            title = "저희 팀 코밋 예쁘게 봐주세요!",
            options = listOf("확인했습니다."),
            selectedOption = answer3,
            onOptionSelected = { answer3 = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. 이미지 + 텍스트 섹션
        CommissionImageTextSection(
            text = text,
            onTextChange = { text = it },
            images = images,
            onAddClick = { /* TODO */ },
            onRemoveClick = { index -> images.removeAt(index) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 5. 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFD9D9D9))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 6. 신청하기 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "신청하기",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun CommissionFormScreenPreview() {
    CommitTheme {
        CommissionFormScreen()
    }
}
