package com.example.commit.ui.request.form

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.net.Uri.fromFile
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.viewmodel.CommissionFormState
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.ImageUploadState
import com.example.commit.viewmodel.SubmitState
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun CommissionFormScreen(
    commissionId: String,
    viewModel: CommissionFormViewModel,
    onNavigateToSuccess: () -> Unit = {},
    onNavigateToFormCheck: (existingRequestId: String) -> Unit = {}
) {
    val context = LocalContext.current
    val gson = remember { Gson() }

    val commissionIdInt = remember(commissionId) { commissionId.toIntOrNull() ?: -1 }
    Log.d("CommissionFormScreen", "enter commissionId=$commissionId (int=$commissionIdInt)")

    // 미리보기 이미지 리스트
    val images = remember { mutableStateListOf<Bitmap>() }

    // Bitmap -> Uri (업로드용)
    fun bitmapToUri(bitmap: Bitmap, ctx: Context): Uri {
        val file = File(ctx.cacheDir, "image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { fos -> bitmap.compress(CompressFormat.JPEG, 90, fos) }
        return fromFile(file)
    }

    // API 호출: 스키마 가져오기 (유효 id일 때만)
    LaunchedEffect(commissionIdInt) {
        if (commissionIdInt > 0) {
            viewModel.getCommissionForm(commissionId, context)
        } else {
            Log.w("CommissionFormScreen", "skip getCommissionForm due to invalid id=$commissionId")
        }
    }

    val commissionFormState by viewModel.commissionFormState.collectAsStateWithLifecycle()
    val imageUploadState by viewModel.imageUploadState.collectAsStateWithLifecycle()
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()
    val isUploading by viewModel.isUploading.collectAsStateWithLifecycle()

    // 기본 폼 스키마 (API 미도착시)
    val defaultFormSchema = listOf(
        FormItem(id = 1, label = "당일마감", type = "radio",
            options = listOf(OptionItem("O (+10000P)"), OptionItem("X"))),
        FormItem(id = 2, label = "신청 캐릭터", type = "radio",
            options = listOf(OptionItem("고양이"), OptionItem("햄스터"), OptionItem("캐리커쳐"), OptionItem("랜덤"))),
        FormItem(id = 3, label = "저희 팀 코밋 예쁘게 봐주세요!", type = "check",
            options = listOf(OptionItem("확인했습니다."))),
        FormItem(id = 4, label = "신청 내용", type = "textarea")
    )

    // API 스키마 or 기본 스키마로 매핑
    val formSchema: List<FormItem> = when (val state = commissionFormState) {
        is CommissionFormState.Success -> {
            val fieldsFromApi = state.data?.success?.formSchema?.fields
            if (!fieldsFromApi.isNullOrEmpty()) {
                fieldsFromApi.map { f ->
                    FormItem(
                        id = f.id.toString().toIntOrNull(),
                        type = f.type,
                        label = f.label,
                        options = f.options?.mapNotNull { opt ->
                            val lbl = (opt.label ?: opt.value)?.trim()
                            if (!lbl.isNullOrEmpty()) OptionItem(label = lbl) else null
                        } ?: emptyList()
                    )
                }
            } else defaultFormSchema
        }
        else -> defaultFormSchema
    }

    // label 기반으로 답변을 쌓는 구조
    val formAnswer = remember { mutableStateMapOf<String, Any>() }

    val isFormComplete by remember(formSchema, formAnswer) {
        derivedStateOf {
            val requiredLabels = formSchema
                .filter { it.type in listOf("radio", "check") }
                .map { it.label }
            if (requiredLabels.isEmpty()) true
            else {
                val completed = requiredLabels.count { field ->
                    val v = formAnswer[field]
                    when {
                        field.contains("확인") -> v == "확인했습니다."
                        else -> (v as? String)?.isNotBlank() == true
                    }
                }
                completed >= requiredLabels.size
            }
        }
    }

    // 제출 상태 감지 → FormCheckActivity로 이동 + formSchema/Answer 전달
    LaunchedEffect(submitState) {
        when (submitState) {
            is SubmitState.Success -> {
                val requestIdStr = (submitState as SubmitState.Success).requestId
                val requestIdInt = requestIdStr?.toIntOrNull()?.takeIf { it > 0 } ?: -1
                Log.d("CommissionFormScreen", "Submit.Success requestId(raw)=$requestIdStr, putExtra=$requestIdInt")

                val intent = android.content.Intent(
                    context,
                    com.example.commit.activity.FormCheckActivity::class.java
                )
                intent.putExtra("requestId", requestIdInt)
                intent.putExtra("commissionId", commissionIdInt)
                intent.putExtra("formSchemaJson", gson.toJson(formSchema))
                intent.putExtra("formAnswerJson", gson.toJson(formAnswer.toMap()))
                (context as Activity).startActivity(intent)
            }
            is SubmitState.AlreadySubmitted -> {
                val existingIdStr = (submitState as SubmitState.AlreadySubmitted).existingRequestId
                val existingIdInt = existingIdStr.toIntOrNull()?.takeIf { it > 0 } ?: -1
                Log.d("CommissionFormScreen", "Submit.Already existingRequestId(raw)=$existingIdStr, putExtra=$existingIdInt")

                val intent = android.content.Intent(
                    context,
                    com.example.commit.activity.FormCheckActivity::class.java
                )
                intent.putExtra("requestId", existingIdInt)
                intent.putExtra("commissionId", commissionIdInt)
                intent.putExtra("formSchemaJson", gson.toJson(formSchema))
                intent.putExtra("formAnswerJson", gson.toJson(formAnswer.toMap()))
                (context as Activity).startActivity(intent)
            }
            else -> {}
        }
    }

    when (commissionFormState) {
        is CommissionFormState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CommissionFormState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("오류가 발생했습니다", fontSize = 16.sp, color = Color.Red)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = (commissionFormState as CommissionFormState.Error).message,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        if (commissionIdInt > 0) viewModel.getCommissionForm(commissionId, context)
                    }) {
                        Text("다시 시도")
                    }
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 상단바/헤더는 기존 컴포넌트 사용
                CommissionTopBar(onBackClick = { (context as? Activity)?.finish() })

                val commissionInfo = (commissionFormState as? CommissionFormState.Success)
                    ?.data?.success?.commission

                CommissionHeader(
                    artistName = commissionInfo?.artist?.nickname ?: "작가",
                    commissionTitle = commissionInfo?.title ?: "커미션",
                    thumbnailImageUrl = commissionInfo?.thumbnailImageUrl
                )

                Spacer(Modifier.height(20.dp))
                Divider(thickness = 8.dp, color = Color(0xFFD9D9D9))
                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    var currentIndex = 1

                    // 라디오/체크
                    formSchema.filter { it.type in listOf("radio", "check") }.forEach { item ->
                        Spacer(Modifier.height(12.dp))
                        val selectedOption = formAnswer[item.label] as? String ?: ""
                        CommissionOptionSection(
                            index = currentIndex,
                            title = item.label,
                            options = item.options.map { it.label },
                            selectedOption = selectedOption,
                            onOptionSelected = { formAnswer[item.label] = it }
                        )
                        currentIndex++
                    }

                    // 이미지/파일
                    val imageUploadStateSnapshot = imageUploadState // to avoid smart cast issues
                    formSchema.filter { it.type in listOf("image", "file") }.forEach { item ->
                        Spacer(Modifier.height(12.dp))
                        if (imageUploadStateSnapshot is ImageUploadState.Loading) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        CommissionImageSection(
                            index = currentIndex,
                            images = images,
                            onAddClick = { /* 갤러리/카메라 열기 등 */ },
                            onRemoveClick = { removeIndex -> images.removeAt(removeIndex) },
                            onImageUpload = { uri -> viewModel.uploadImage(uri, context) },
                            onImageAdded = { bitmap ->
                                images.add(bitmap)
                                val imageUri = bitmapToUri(bitmap, context)
                                viewModel.uploadImage(imageUri, context)
                            }
                        )
                        currentIndex++
                    }

                    // 텍스트
                    formSchema.filter { it.type == "textarea" }.forEach { item ->
                        Spacer(Modifier.height(12.dp))
                        CommissionTextareaSection(
                            index = currentIndex,
                            text = formAnswer[item.label] as? String ?: "",
                            onTextChange = { formAnswer[item.label] = it }
                        )
                        currentIndex++
                    }

                    Spacer(Modifier.height(20.dp))

                    // 신청 버튼
                    Button(
                        onClick = {
                            if (isFormComplete && commissionIdInt > 0) {
                                viewModel.submitCommissionRequest(
                                    commissionId = commissionId,
                                    answersByLabel = formAnswer,
                                    context = context
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFormComplete) Color(0xFF4D4D4D) else Color(0xFFEDEDED),
                            contentColor = if (isFormComplete) Color.White else Color(0xFFB0B0B0)
                        ),
                        enabled = isFormComplete && !isUploading
                    ) {
                        Text(
                            text = if (isUploading) "업로드 중..." else "신청하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun CommissionFormScreenPreview() {
    CommitTheme {
        CommissionFormScreen(
            commissionId = "1",
            viewModel = CommissionFormViewModel()
        )
    }
}
