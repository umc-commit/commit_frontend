package com.example.commit.ui.FormCheck

import android.widget.Toast
import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.data.model.RequestItem
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.SubmittedFormState

@Composable
fun FormCheckScreen(
    chatItem: ChatItem,
    requestItem: RequestItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onBackClick: () -> Unit,
    viewModel: CommissionFormViewModel
) {
    val context = LocalContext.current

    // ----- 기본 스키마/답변 (API 실패/미도착 시 대체) -----
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
    val defaultFormAnswer = mapOf(
        "당일마감" to "O (+10000P)",
        "신청 캐릭터" to "고양이",
        "저희 팀 코밋 예쁘게 봐주세요!" to "확인했습니다.",
        "신청 내용" to "귀여운 고양이 그림 부탁드립니다"
    )

    // ----- ViewModel 상태 구독 -----
    val remoteSchema by viewModel.submittedFormSchemaUi.collectAsStateWithLifecycle()
    val submittedState by viewModel.submittedFormState.collectAsStateWithLifecycle()

    // 로그 확인용
    LaunchedEffect(Unit) {
        Log.d("FormCheckScreen", "entered; remoteSchema.size=${remoteSchema.size}")
    }
    LaunchedEffect(remoteSchema) {
        Log.d("FormCheckScreen", "remoteSchema updated size=${remoteSchema.size}")
    }
    LaunchedEffect(submittedState) {
        Log.d("FormCheckScreen", "submittedState=$submittedState")
        val msg = (submittedState as? SubmittedFormState.Error)?.message
        if (!msg.isNullOrBlank()) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // ----- 최종 사용 스키마/답변 (원격 > 전달 > 기본) -----
    val usedFormSchema = when {
        remoteSchema.isNotEmpty() -> remoteSchema
        formSchema.isNotEmpty() -> formSchema
        else -> defaultFormSchema
    }
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
            requestItem = requestItem
        )

        // 스크롤 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            if (submittedState is SubmittedFormState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            FormCheckSection(
                item = chatItem,
                formSchema = usedFormSchema,
                formAnswer = usedFormAnswer,
                onBackClick = onBackClick
            )
        }

        // 하단 취소 버튼
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
            Text(text = "취소하기", fontSize = 16.sp, color = Color.White)
        }
    }
}
