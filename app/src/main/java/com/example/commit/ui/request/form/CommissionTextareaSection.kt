package com.example.commit.ui.request.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun CommissionTextareaSection(
    index: Int,
    text: String,
    onTextChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // 텍스트 입력 + 글자수 표시를 감싸는 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .width(340.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 5000) onTextChange(it)
                },
                placeholder = { Text("내용을 입력해주세요") },
                modifier = Modifier
                    .fillMaxSize()
            )

            // 오른쪽 하단 글자수 텍스트
            Text(
                text = "${text.length} / 5000",
                style = TextStyle(
                    fontSize = 8.sp,
                    color = Color(0xFFB0B0B0)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 15.dp, bottom = 15.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommissionTextareaSectionPreview() {
    var text by remember { mutableStateOf("") }

    CommitTheme {
        CommissionTextareaSection(
            index = 1,
            text = text,
            onTextChange = { text = it }
        )
    }
}
