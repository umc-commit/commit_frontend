package com.example.commit.ui.request.form

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.viewmodel.CommissionFormState
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.ImageUploadState
import com.example.commit.viewmodel.SubmitState
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap.CompressFormat
import android.net.Uri.fromFile
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CommissionFormScreen(
    commissionId: String,
    viewModel: CommissionFormViewModel,
    onNavigateToSuccess: () -> Unit
) {
    val context = LocalContext.current

    // 미리보기 이미지 리스트
    val images = remember { mutableStateListOf<Bitmap>() }

    // Bitmap -> Uri (업로드용)
    fun bitmapToUri(bitmap: Bitmap, ctx: Context): Uri {
        val file = File(ctx.cacheDir, "image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { fos -> bitmap.compress(CompressFormat.JPEG, 90, fos) }
        return fromFile(file)
    }

    // API 호출
    LaunchedEffect(commissionId) {
        viewModel.getCommissionForm(commissionId, context)
    }

    val commissionFormState by viewModel.commissionFormState.collectAsState()
    val imageUploadState by viewModel.imageUploadState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    // 기본 폼 스키마 (API 미도착시)
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
            options = listOf(OptionItem("고양이"), OptionItem("햄스터"), OptionItem("캐리커쳐"), OptionItem("랜덤"))
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
            type = "textarea" // ← 반드시 textarea
        )
    )

    // API 스키마 or 기본 스키마
    val formSchema = when (commissionFormState) {
        is CommissionFormState.Success -> {
            val response = (commissionFormState as CommissionFormState.Success).data
            val formSchemaData = response?.success?.formSchema
            Log.d("FormDebug", "API 응답 받음 - formSchema: $formSchemaData")

            if (formSchemaData?.fields != null) {
                runCatching {
                    // API 응답 + 기본 라디오/체크박스 옵션 병합
                    val apiFields = formSchemaData.fields.map { f ->
                        FormItem(
                            id = f.id.toIntOrNull(),
                            type = f.type,
                            label = f.label,
                            options = f.options?.map { OptionItem(it.label) } ?: emptyList()
                        )
                    }
                    
                    val merged = mutableListOf<FormItem>()
                    // 기본 라디오/체크박스 먼저 추가
                    merged += defaultFormSchema.filter { it.type in listOf("radio", "check") }
                    // API 응답 필드 추가 (중복 라벨 제외)
                    apiFields.forEach { apiItem ->
                        if (merged.none { it.label == apiItem.label }) {
                            merged += apiItem
                        }
                    }
                    merged
                }.getOrElse {
                    Log.e("FormSchema", "formSchema 파싱 오류: ${it.message}")
                    defaultFormSchema
                }
            } else defaultFormSchema
        }
        is CommissionFormState.Error -> {
            Log.w("CommissionFormScreen", "API 오류 발생, 기본 폼 사용: ${(commissionFormState as CommissionFormState.Error).message}")
            defaultFormSchema
        }
        is CommissionFormState.Loading -> {
            Log.d("CommissionFormScreen", "API 로딩 중, 기본 폼 사용")
            defaultFormSchema
        }
        else -> defaultFormSchema
    }

    Log.d("FormDebug", "최종 formSchema: $formSchema")

    val formAnswer = remember { mutableStateMapOf<String, Any>() }

    val isFormComplete by remember(formSchema, formAnswer) {
        derivedStateOf {
            // API 응답에 따라 동적으로 완성 조건 결정
            val requiredFields = formSchema.filter { it.type in listOf("radio", "check") }
            val requiredLabels = requiredFields.map { it.label }
            
            if (requiredLabels.isEmpty()) {
                // 필수 필드가 없으면 항상 완성된 것으로 간주
                true
            } else {
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
                    Button(onClick = { viewModel.getCommissionForm(commissionId, context) }) {
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
                CommissionTopBar(
                    onBackClick = {
                        // 뒤로가기 버튼 클릭 시 이전 화면으로 돌아가기
                        try {
                            when (context) {
                                is FragmentActivity -> {
                                    if (context.supportFragmentManager.backStackEntryCount > 0) {
                                        context.supportFragmentManager.popBackStack()
                                    } else {
                                        context.finish()
                                    }
                                }
                                is androidx.activity.ComponentActivity -> {
                                    context.finish()
                                }
                                else -> {
                                    // 일반적인 경우 finish() 호출
                                    (context as? android.app.Activity)?.finish()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CommissionFormScreen", "뒤로가기 처리 실패: ${e.message}")
                            // 예외 발생 시 finish() 시도
                            try {
                                (context as? android.app.Activity)?.finish()
                            } catch (finishException: Exception) {
                                Log.e("CommissionFormScreen", "finish() 호출 실패: ${finishException.message}")
                            }
                        }
                    }
                )

                val commissionInfo = (commissionFormState as? CommissionFormState.Success)
                    ?.data?.success?.commission

                CommissionHeader(
                    artistName = commissionInfo?.artist?.nickname ?: "키르",
                    commissionTitle = commissionInfo?.title ?: "낙서 타입 커미션"
                )
                Spacer(Modifier.height(20.dp))
                Divider(thickness = 8.dp, color = Color(0xFFD9D9D9))
                Spacer(Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    val isUploading by viewModel.isUploading.collectAsState()
                    val uploadedImageUrls by viewModel.uploadedImageUrls.collectAsState()



                    // 먼저 라디오/체크박스 필드들 처리
                    formSchema.filter { it.type in listOf("radio", "check") }.forEachIndexed { index, item ->
                        Log.d("FormDebug", "라디오/체크박스 처리 - index: $index, item: $item")
                        Spacer(Modifier.height(12.dp))
                        
                        val selectedOption = formAnswer[item.label] as? String ?: ""
                        CommissionOptionSection(
                            index = index + 1,
                            title = item.label,
                            options = item.options.map { it.label },
                            selectedOption = selectedOption,
                            onOptionSelected = { formAnswer[item.label] = it }
                        )
                    }

                    // 그 다음 이미지 필드들 처리
                    formSchema.filter { it.type in listOf("image", "file") }.forEachIndexed { index, item ->
                        Log.d("FormDebug", "이미지 필드 처리 - index: $index, item: $item")
                        Spacer(Modifier.height(12.dp))
                        
                        when (imageUploadState) {
                            is ImageUploadState.Loading -> {
                                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator()
                                }
                            }
                            else -> {}
                        }

                        CommissionImageSection(
                            index = index + 1,
                            images = images,
                            onAddClick = { /* 필요시 부가동작 */ },
                            onRemoveClick = { removeIndex -> images.removeAt(removeIndex) },
                            onImageUpload = { uri -> viewModel.uploadImage(uri, context) },
                            onImageAdded = { bitmap ->
                                images.add(bitmap)
                                val imageUri = bitmapToUri(bitmap, context)
                                viewModel.uploadImage(imageUri, context)
                            }
                        )
                    }

                    // 마지막에 텍스트박스 필드들 처리
                    formSchema.filter { it.type == "textarea" }.forEachIndexed { index, item ->
                        Log.d("FormDebug", "텍스트박스 처리 - index: $index, item: $item")
                        Spacer(Modifier.height(12.dp))
                        
                        CommissionTextareaSection(
                            index = index + 1,
                            text = formAnswer[item.label] as? String ?: "",
                            onTextChange = { formAnswer[item.label] = it }
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    when (submitState) {
                        is SubmitState.Loading -> {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is SubmitState.Error -> {
                            val errorMessage = (submitState as SubmitState.Error).message
                            if (errorMessage.contains("이미 신청한 커미션")) {
                                // 이미 신청한 경우: 토스트 메시지만 표시
                                LaunchedEffect(Unit) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "이미 신청한 커미션입니다",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    
                                    // 잠시 후 이전 페이지로 돌아가기
                                    kotlinx.coroutines.delay(1000)
                                    if (context is FragmentActivity) {
                                        context.supportFragmentManager.popBackStack()
                                    } else if (context is androidx.activity.ComponentActivity) {
                                        context.finish()
                                    }
                                }
                            } else {
                                // 일반적인 오류는 표시하지 않음
                            }
                        }
                        is SubmitState.Success -> {
                            LaunchedEffect(Unit) {
                                // 제출 성공 토스트 메시지 표시
                                android.widget.Toast.makeText(
                                    context,
                                    "신청이 성공적으로 제출되었습니다!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                
                                // 잠시 후 이전 페이지로 돌아가기
                                kotlinx.coroutines.delay(1000)
                                if (context is FragmentActivity) {
                                    context.supportFragmentManager.popBackStack()
                                } else if (context is androidx.activity.ComponentActivity) {
                                    context.finish()
                                }
                            }
                        }
                        is SubmitState.Error -> {
                            val errorMessage = (submitState as SubmitState.Error).message
                            val isAlreadySubmitted = errorMessage.contains("이미 신청한 커미션입니다")
                            
                            if (isAlreadySubmitted) {
                                // 이미 신청한 경우: 토스트 메시지 표시 후 이전 화면으로 이동
                                LaunchedEffect(Unit) {
                                    android.widget.Toast.makeText(
                                        context,
                                        "이미 신청한 커미션입니다",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    
                                    // 잠시 후 이전 페이지로 돌아가기
                                    kotlinx.coroutines.delay(1000)
                                    if (context is FragmentActivity) {
                                        context.supportFragmentManager.popBackStack()
                                    } else if (context is androidx.activity.ComponentActivity) {
                                        context.finish()
                                    }
                                }
                            }
                            // 일반적인 오류는 표시하지 않음
                        }
                        else -> {}
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (isFormComplete) {
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
                            contentColor = Color.Unspecified
                        ),
                        enabled = isFormComplete
                    ) {
                        Text(
                            text = "신청하기",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isFormComplete) Color.White else Color(0xFFB0B0B0)
                        )
                    }

                    Spacer(Modifier.height(30.dp))
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
            viewModel = CommissionFormViewModel(),
            onNavigateToSuccess = {}
        )
    }
}
