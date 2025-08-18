package com.example.commit.ui.FormCheck

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.layout.systemBarsPadding
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
    val activity = context as? Activity

    // 🔹 인텐트에서 requestId/commissionId 확보 (requestId 우선)
    val requestIdExtra = remember { activity?.intent?.getIntExtra("requestId", -1) ?: -1 }
    val commissionIdExtra = remember { activity?.intent?.getIntExtra("commissionId", -1) ?: -1 }

    // ViewModel 상태
    val remoteSchema by viewModel.submittedFormSchemaUi.collectAsStateWithLifecycle()
    val remoteAnswer by viewModel.submittedFormAnswerUi.collectAsStateWithLifecycle()
    val submittedState by viewModel.submittedFormState.collectAsStateWithLifecycle()

    // 🔹 최초 진입 시: 데이터 없고 로딩중이 아닐 때만 호출 (중복 방지)
    LaunchedEffect(requestIdExtra) {
        if (requestIdExtra > 0 &&
            remoteSchema.isEmpty() &&
            submittedState !is SubmittedFormState.Loading
        ) {
            Log.d("FormCheckScreen", "fetch by requestId=$requestIdExtra")
            viewModel.getSubmittedRequestForms(requestIdExtra.toString(), context)
        } else {
            Log.d(
                "FormCheckScreen",
                "skip fetch (requestId=$requestIdExtra, schema=${remoteSchema.size}, state=$submittedState)"
            )
        }
    }

    // 로그/에러 토스트
    LaunchedEffect(remoteSchema) {
        Log.d("FormCheckScreen", "remoteSchema updated size=${remoteSchema.size}")
    }
    LaunchedEffect(submittedState) {
        Log.d("FormCheckScreen", "submittedState=$submittedState")
        val msg = (submittedState as? SubmittedFormState.Error)?.message
        if (!msg.isNullOrBlank()) Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    //  최종 그릴 스키마/답변 (원격 > 전달 > 기본)
    val usedFormSchema = when {
        remoteSchema.isNotEmpty() -> remoteSchema
        formSchema.isNotEmpty() -> formSchema
        else -> defaultFormSchema
    }
    val usedFormAnswer: Map<String, Any> = when {
        remoteAnswer.isNotEmpty() -> remoteAnswer
        formAnswer.isNotEmpty() -> formAnswer
        else -> defaultFormAnswer
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        FormCheckTopBar(
            onBackClick = onBackClick,
            chatItem = chatItem,
            requestItem = requestItem
        )

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

/* Fallback 기본 스키마/답변 */
private val defaultFormSchema: List<FormItem> = listOf(
    FormItem(id = 1, label = "당일마감", type = "radio", options = listOf(OptionItem("O (+10000P)"), OptionItem("X"))),
    FormItem(id = 2, label = "신청 캐릭터", type = "radio", options =
        listOf(OptionItem("고양이"), OptionItem("햄스터"), OptionItem("캐리커쳐"), OptionItem("랜덤"))),
    FormItem(id = 3, label = "저희 팀 코밋 예쁘게 봐주세요!", type = "check", options = listOf(OptionItem("확인했습니다."))),
    FormItem(id = 4, label = "신청 내용", type = "textarea", options = emptyList())
)
private val defaultFormAnswer: Map<String, Any> = emptyMap()
