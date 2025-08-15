package com.example.commit.ui.FormCheck

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.data.model.Artist
import com.example.commit.data.model.Commission
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.data.model.RequestItem
import com.example.commit.data.model.entities.ChatItem
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

    // API 미도착 시 기본 폼 스키마
    val defaultFormSchema = listOf(
        FormItem(
            id = 1,
            label = "당일마감",
            type = "radio",
            options = listOf(OptionItem("O (+10000P)"), OptionItem("X"))
        ),
        FormItem(
            id = 2,
            label = "신청 캐릭터",
            type = "radio",
            options = listOf(
                OptionItem("고양이"),
                OptionItem("햄스터"),
                OptionItem("캐리커쳐"),
                OptionItem("랜덤")
            )
        ),
        FormItem(
            id = 3,
            label = "저희 팀 코밋 예쁘게 봐주세요!",
            type = "check",
            options = listOf(OptionItem("확인했습니다."))
        ),
        FormItem(
            id = 4,
            label = "신청 내용",
            type = "textarea"
        )
    )

    // API 미도착 시 기본 답변
    val defaultFormAnswer = mapOf(
        "당일마감" to "O (+10000P)",
        "신청 캐릭터" to "고양이",
        "저희 팀 코밋 예쁘게 봐주세요!" to "확인했습니다.",
        "신청 내용" to "귀여운 고양이 그림 부탁드립니다"
    )

    // 폼/답변 최종 사용값 결정
    val usedFormSchema = if (formSchema.isEmpty()) defaultFormSchema else formSchema
    val usedFormAnswer = if (formAnswer.isEmpty()) defaultFormAnswer else formAnswer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
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
                formSchema = usedFormSchema,
                formAnswer = usedFormAnswer, // ✅ 여기서 변경
                onBackClick = onBackClick
            )
        }

        // 하단 고정 취소 버튼
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
        val dummyChatItem = ChatItem(
            profileImageRes = R.drawable.ic_profile,
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
            artist = Artist(id = 1, nickname = "키르"),
            commission = Commission(id = 1)
        )

        val dummyFormSchema = emptyList<FormItem>()
        val dummyFormAnswer = mapOf<String, Any>(
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
