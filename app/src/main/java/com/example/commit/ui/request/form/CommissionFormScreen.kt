package com.example.commit.ui.request.form

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
    // 상태 정의
    val images = remember { mutableStateListOf<Bitmap>() }
    var text by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .width(400.dp) // 💡 너비 고정
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 0. TopBar
        CommissionTopBar()

        // 1. Header
        CommissionHeader()

        Spacer(modifier = Modifier.height(20.dp))

        // 2. 질문 섹션
        CommissionOptionSection(
            index = 1,
            title = "커미션을 어떻게 알게 되셨나요?",
            isChecked = false,
            onCheckedChange = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. 이미지 + 텍스트 섹션
        CommissionImageTextSection(
            text = text,
            onTextChange = { text = it },
            images = images,
            onAddClick = { /* TODO */ },
            onRemoveClick = { index -> images.removeAt(index) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. 구분선
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFD9D9D9))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 5. 신청하기 버튼
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

@Preview(showBackground = true, widthDp = 400)
@Composable
fun CommissionFormScreenPreview() {
    CommitTheme {
        CommissionFormScreen()
    }
}
