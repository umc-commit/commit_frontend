package com.example.commit.ui.request.form

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import android.net.Uri.fromFile
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.viewmodel.CommissionFormState
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.ImageUploadState
import com.example.commit.viewmodel.SubmitState
import java.io.File
import java.io.FileOutputStream

@Composable
fun CommissionFormScreen(
    commissionId: String,
    viewModel: CommissionFormViewModel,
    onNavigateToSuccess: () -> Unit,
    onNavigateToFormCheck: (existingRequestId: String) -> Unit
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
            type = "textarea"
        ),
        FormItem(
            id = 5,
            label = "참고 이미지",
            type = "file"
        )
    )

    // API 스키마 or 기본 스키마
    val formSchema = when (val state = commissionFormState) {
        is CommissionFormState.Success -> {
            val fieldsFromApi = if (commissionId == "1") state.data?.success?.formSchema?.fields else null
            if (!fieldsFromApi.isNullOrEmpty()) {
                fieldsFromApi.map { f ->
                    FormItem(
                        id = f.id?.toIntOrNull(),
                        type = f.type,
                        label = f.label,
                        options = f.options
                            ?.mapNotNull { opt ->
                                val labelStr = opt.label?.trim()
                                val valueStr = opt.value?.trim()
                                when {
                                    // label & value 모두 존재
                                    !labelStr.isNullOrEmpty() && !valueStr.isNullOrEmpty() ->
                                        OptionItem(label = labelStr, value = valueStr)
                                    // label만 존재 → value는 기본값(label) 사용
                                    !labelStr.isNullOrEmpty() ->
                                        OptionItem(label = labelStr)
                                    else -> null
                                }
                            }
                            ?: emptyList()
                    )
                }
            } else {
                defaultFormSchema
            }
        }
        is CommissionFormState.Error -> defaultFormSchema
        is CommissionFormState.Loading -> defaultFormSchema
        else -> defaultFormSchema
    }

    Log.d("FormDebug", "최종 formSchema: $formSchema")

    val formAnswer = remember { mutableStateMapOf<String, Any>() }

    val isFormComplete by remember(formSchema, formAnswer) {
        derivedStateOf {
            val requiredFields = formSchema.filter { it.type in listOf("radio", "check") }
            val requiredLabels = requiredFields.map { it.label }
            if (requiredLabels.isEmpty()) return@derivedStateOf true

            val completed = requiredLabels.count { field ->
                when (val v = formAnswer[field]) {
                    is String -> v.isNotBlank()
                    else -> false
                }
            }
            completed >= requiredLabels.size
        }
    }

    when (commissionFormState) {
        is CommissionFormState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

//        is CommissionFormState.Error -> {
//            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Text("오류가 발생했습니다", fontSize = 16.sp, color = Color.Red)
//                    Spacer(Modifier.height(8.dp))
//                    Text(
//                        text = (commissionFormState as CommissionFormState.Error).message,
//                        fontSize = 14.sp,
//                        color = Color.Gray
//                    )
//                    Spacer(Modifier.height(16.dp))
//                    Button(onClick = { viewModel.getCommissionForm(commissionId, context) }) {
//                        Text("다시 시도")
//                    }
//                }
//            }
//        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CommissionTopBar(
                    onBackClick = {
                        try {
                            when (context) {
                                is FragmentActivity -> {
                                    if (context.supportFragmentManager.backStackEntryCount > 0) {
                                        context.supportFragmentManager.popBackStack()
                                    } else {
                                        context.finish()
                                    }
                                }
                                is androidx.activity.ComponentActivity -> context.finish()
                                else -> (context as? Activity)?.finish()
                            }
                        } catch (e: Exception) {
                            Log.e("CommissionFormScreen", "뒤로가기 처리 실패: ${e.message}")
                            (context as? Activity)?.finish()
                        }
                    }
                )

                val commissionInfo = (commissionFormState as? CommissionFormState.Success)
                    ?.data?.success?.commission

                CommissionHeader(
                    artistName = commissionInfo?.artist?.nickname ?: "키르",
                    commissionTitle = commissionInfo?.title ?: "낙서 타입 커미션",
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
                    val isUploading by viewModel.isUploading.collectAsState()
                    val uploadedImageUrls by viewModel.uploadedImageUrls.collectAsState()

                    var currentIndex = 1

                    // 1) 라디오/체크박스
                    formSchema.filter { it.type in listOf("radio", "check") }.forEach { item ->
                        Spacer(Modifier.height(12.dp))
                        val selectedValue = formAnswer[item.label] as? String ?: ""
                        // selectedValue를 label로 변환하여 UI에 전달
                        val selectedOption = item.options.find { it.value == selectedValue }?.label ?: selectedValue
                        CommissionOptionSection(
                            index = currentIndex,
                            title = item.label,
                            options = item.options.map { it.label },
                            selectedOption = selectedOption,
                            onOptionSelected = { selectedLabel ->
                                val option = item.options.find { it.label == selectedLabel }
                                val valueToStore = option?.value ?: selectedLabel
                                formAnswer[item.label] = valueToStore
                            }
                        )
                        currentIndex++
                    }

                    // 2) 이미지/파일
                    formSchema.filter { it.type in listOf("image", "file") }.forEach { _ ->
                        Spacer(Modifier.height(12.dp))

                        if (imageUploadState is ImageUploadState.Loading || (isUploading && images.isNotEmpty())) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        CommissionImageSection(
                            index = currentIndex,
                            images = images,
                            onAddClick = { /* no-op */ },
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

                    // 3) 텍스트박스
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

                    when (submitState) {
                        is SubmitState.Loading -> {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        is SubmitState.Success, is SubmitState.AlreadySubmitted -> {
                            LaunchedEffect("success") {
                                android.widget.Toast.makeText(
                                    context,
                                    "신청에 성공했습니다",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                kotlinx.coroutines.delay(1000)
                                if (context is FragmentActivity) {
                                    context.supportFragmentManager.popBackStack()
                                } else if (context is androidx.activity.ComponentActivity) {
                                    context.finish()
                                }
                                // onNavigateToSuccess()
                            }
                        }

                        is SubmitState.Error -> {
                            val msg = (submitState as SubmitState.Error).message
                            val isAlready = msg.contains("이미 신청한 커미션")
                            if (isAlready) {
                                LaunchedEffect("error-already") {
                                    android.widget.Toast.makeText(
                                        context,
                                        "신청에 성공했습니다",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                    kotlinx.coroutines.delay(1000)
                                    if (context is FragmentActivity) {
                                        context.supportFragmentManager.popBackStack()
                                    } else if (context is androidx.activity.ComponentActivity) {
                                        context.finish()
                                    }
                                    // onNavigateToFormCheck(existingRequestId)
                                }
                            }
                        }

                        else -> Unit
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
            onNavigateToSuccess = {},
            onNavigateToFormCheck = { /* no-op for preview */ }
        )
    }
}
