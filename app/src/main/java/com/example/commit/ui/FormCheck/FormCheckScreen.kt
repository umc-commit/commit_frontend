package com.example.commit.ui.FormCheck

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun FormCheckScreen(
    chatItem: ChatItem,
    requestItem: RequestItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding() // 시스템 바에 대한 패딩 추가
    ) {
        // 상단 고정 TopBar
        FormCheckTopBar(
            onBackClick = onBackClick,
            chatItem = chatItem,
            requestItem = requestItem,
        )

        // 스크롤 가능한 Section
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            FormCheckSection(
                item = chatItem,
                formSchema = formSchema,
                formAnswer = formAnswer,
                onBackClick = onBackClick
            )
        }
        
        // 하단 고정 취소 버튼 (ChatDeleteScreen과 동일한 스타일)
        Button(
            onClick = {
                Toast.makeText(context, "취소되었습니다", Toast.LENGTH_SHORT).show()
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4D4D4D),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "취소하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormCheckScreen() {
    CommitTheme {
        // 더미 데이터 생성
        val dummyChatItem = ChatItem(
            profileImageRes = com.example.commit.R.drawable.ic_profile,
            name = "키르",
            message = "최근 메시지",
            time = "2시간 전",
            isNew = false,
            title = "낙서 타임 커미션"
        )
        
        val dummyRequestItem = RequestItem(
            requestId = 1,
            status = "진행중",
            title = "낙서 타임 커미션",
            price = 50000,
            thumbnailImageUrl = "",
            progressPercent = 50,
            createdAt = "2023-12-01",
            artist = com.example.commit.data.model.Artist(
                id = 1,
                nickname = "키르"
            ),
            commission = com.example.commit.data.model.Commission(
                id = 1
            )
        )
        
        val dummyFormSchema = listOf(
            FormItem(
                id = 1,
                type = "textarea",
                label = "신청 내용",
                options = emptyList()
            ),
            FormItem(
                id = 2,
                type = "file",
                label = "참고 이미지",
                options = emptyList()
            ),
            FormItem(
                id = 3,
                type = "radio",
                label = "당일마감",
                options = listOf(
                    com.example.commit.data.model.OptionItem("O"),
                    com.example.commit.data.model.OptionItem("X")
                )
            )
        )
        
        val dummyFormAnswer = mapOf(
            "신청 내용" to "귀여운 고양이 그림 부탁드립니다",
            "당일마감" to "O"
        )
        
        FormCheckScreen(
            chatItem = dummyChatItem,
            requestItem = dummyRequestItem,
            formSchema = dummyFormSchema,
            formAnswer = dummyFormAnswer,
            onBackClick = { /* Preview */ }
        )
    }
}
