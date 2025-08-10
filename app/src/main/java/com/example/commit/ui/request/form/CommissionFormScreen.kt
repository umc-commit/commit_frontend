package com.example.commit.ui.request.form

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.commit.R
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.viewmodel.CommissionFormState
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.ImageUploadState
import com.example.commit.viewmodel.SubmitState
import com.example.commit.ui.request.form.CommissionTextareaSection
import com.example.commit.ui.request.form.CommissionImageSection
import java.io.File
import java.io.FileOutputStream

@Composable
fun CommissionFormScreen(
    commissionId: String,
    viewModel: CommissionFormViewModel,
    onNavigateToSuccess: () -> Unit
) {
    val context = LocalContext.current

    // 미리보기 이미지 리스트를 런처보다 먼저 선언 (안전한 참조)
    val images = remember { mutableStateListOf<Bitmap>() }

    // 이미지 선택 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { picked ->
            // 미리보기용 비트맵 생성
            val bmp = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val src = ImageDecoder.createSource(context.contentResolver, picked)
                    ImageDecoder.decodeBitmap(src)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, picked)
                }
            } catch (e: Exception) {
                null
            }
            // 미리보기 리스트 추가
            bmp?.let { images.add(it) }
            // 업로드 실행
            viewModel.uploadImage(picked, context)
        }
    }

    // Bitmap → Uri (이미지 추가 시 업로드용)
    fun bitmapToUri(bitmap: Bitmap, ctx: Context): Uri {
        val file = File(ctx.cacheDir, "image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { fos -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos) }
        return Uri.fromFile(file)
    }

    // API 호출
    LaunchedEffect(commissionId) {
        viewModel.getCommissionForm(commissionId, context)
    }

    val commissionFormState by viewModel.commissionFormState.collectAsState()
    val imageUploadState by viewModel.imageUploadState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    // 기본 폼 스키마
    val defaultFormSchema = listOf(
        FormItem(
            label = "당일마감",
            type = "radio",
            options = listOf(OptionItem("O (+10000P)"), OptionItem("X"))
        ),
        FormItem(
            label = "신청 캐릭터",
            type = "radio",
            options = listOf(OptionItem("고양이"), OptionItem("햄스터"), OptionItem("캐리커쳐"), OptionItem("랜덤"))
        ),
        FormItem(
            label = "저희 팀 코밋 예쁘게 봐주세요!",
            type = "check",
            options = listOf(OptionItem("확인했습니다."))
        ),
        FormItem(
            label = "신청 내용",
            type = "image" // ← 기본은 image
        )
    )

    // API 스키마 or 기본 스키마
    val formSchema = when (commissionFormState) {
        is CommissionFormState.Success -> {
            val response = (commissionFormState as CommissionFormState.Success).data
            val formSchemaData = response.success?.formSchema
            Log.d("FormDebug", "API 응답 받음 - formSchema: $formSchemaData")

            if (formSchemaData?.fields != null) {
                try {
                    val parsed = formSchemaData.fields.map { field ->
                        FormItem(
                            type = field.type,
                            label = field.label,
                            options = field.options?.map { OptionItem(it.label) } ?: emptyList()
                        )
                    }
                    val merged = mutableListOf<FormItem>()
                    // 기본 라디오/체크 우선
                    merged += defaultFormSchema.filter { it.type in listOf("radio", "check") }
                    // 중복되지 않는 항목만 추가
                    parsed.forEach { apiItem ->
                        if (merged.none { it.label == apiItem.label }) merged += apiItem
                    }
                    merged
                } catch (e: Exception) {
                    Log.e("FormSchema", "formSchema 파싱 오류: ${e.message}")
                    defaultFormSchema
                }
            } else {
                Log.d("FormDebug", "fields가 null이므로 기본 스키마 사용")
                defaultFormSchema
            }
        }
        else -> {
            Log.d("FormDebug", "API 응답이 아직 없으므로 기본 스키마 사용")
            defaultFormSchema
        }
    }

    Log.d("FormDebug", "최종 formSchema: $formSchema")

    val formAnswer = remember { mutableStateMapOf<String, Any>() }

    val isFormComplete by remember(formSchema, formAnswer) {
        derivedStateOf {
            val required = formSchema.filter { it.type in listOf("radio", "check") }.map { it.label }
            val completed = required.count { field ->
                val v = formAnswer[field]
                when {
                    field.contains("확인") -> v == "확인했습니다."
                    else -> (v as? String)?.isNotBlank() == true
                }
            }
            completed >= 1
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
                        Log.d("FormDebug", "뒤로가기 버튼 클릭됨")
                        if (context is FragmentActivity) {
                            context.supportFragmentManager.popBackStack()
                        } else if (context is androidx.activity.ComponentActivity) {
                            context.finish()
                        }
                    }
                )

                // 커미션 정보
                val commissionInfo = when (commissionFormState) {
                    is CommissionFormState.Success -> {
                        val response = (commissionFormState as CommissionFormState.Success).data
                        response.success?.commission
                    }
                    else -> null
                }

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

                    if (isUploading) {
                        Text(
                            text = "이미지 업로드 중...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (uploadedImageUrls.isNotEmpty()) {
                        Text(
                            text = "업로드된 이미지: ${uploadedImageUrls.size}개",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    formSchema.forEachIndexed { index, item ->
                        Log.d("FormDebug", "폼 아이템 처리 시작 - index: $index, item: $item")
                        Spacer(Modifier.height(12.dp))
                        when (item.type) {
                            "radio", "check" -> {
                                val selectedOption = formAnswer[item.label] as? String ?: ""
                                CommissionOptionSection(
                                    index = index + 1,
                                    title = item.label,
                                    options = item.options.map { it.label },
                                    selectedOption = selectedOption,
                                    onOptionSelected = { formAnswer[item.label] = it }
                                )
                            }

                            "textarea" -> {
                                CommissionTextareaSection(
                                    index = index + 1,
                                    text = formAnswer[item.label] as? String ?: "",
                                    onTextChange = { formAnswer[item.label] = it }
                                )
                            }

                            // ⬇️ "image"와 "file"을 동일하게 처리: 텍스트 박스 + 이미지
                            "image", "file" -> {
                                when (imageUploadState) {
                                    is ImageUploadState.Loading -> {
                                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                    is ImageUploadState.Error -> {
                                        Text(
                                            text = (imageUploadState as ImageUploadState.Error).message,
                                            color = Color.Red,
                                            fontSize = 12.sp
                                        )
                                    }
                                    is ImageUploadState.Success -> {
                                        Text(
                                            text = "이미지 업로드 성공: ${(imageUploadState as ImageUploadState.Success).data.success?.image_url}",
                                            color = Color.Green,
                                            fontSize = 12.sp
                                        )
                                    }
                                    else -> {}
                                }
                                // 참고 이미지
                                CommissionImageSection(
                                    index = index + 1,
                                    images = images,
                                    onAddClick = { /* 추가 부가동작이 필요하면 사용 (실제 추가는 섹션 내부 런처에서 처리됨) */ },
                                    onRemoveClick = { removeIndex -> images.removeAt(removeIndex) },
                                    onImageUpload = { uri -> viewModel.uploadImage(uri, context) },
                                    onImageAdded = { bitmap ->
                                        images.add(bitmap)
                                        val imageUri = bitmapToUri(bitmap, context)
                                        viewModel.uploadImage(imageUri, context)
                                    }
                                )

                                // 신청 내용 (텍스트)
                                CommissionTextareaSection(
                                    index = index,
                                    text = formAnswer[item.label] as? String ?: "",
                                    onTextChange = { text -> formAnswer[item.label] = text }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // 제출 상태 처리
                    when (submitState) {
                        is SubmitState.Loading -> {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }

                        is SubmitState.Error -> {
                            val errorMessage = (submitState as SubmitState.Error).message
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )

                            if (errorMessage.contains("이미 신청한 커미션")) {
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        val fragment = FragmentPostChatDetail.newInstance(
                                            chatName = commissionFormState.let { state ->
                                                if (state is CommissionFormState.Success) {
                                                    state.data.success?.commission?.artist?.nickname ?: "작가"
                                                } else "작가"
                                            },
                                            authorName = commissionFormState.let { state ->
                                                if (state is CommissionFormState.Success) {
                                                    state.data.success?.commission?.title ?: "커미션"
                                                } else "커미션"
                                            },
                                            commissionId = commissionId.toIntOrNull() ?: 1,
                                            hasSubmittedApplication = true
                                        )
                                        if (context is FragmentActivity) {
                                            context.supportFragmentManager.beginTransaction()
                                                .replace(android.R.id.content, fragment)
                                                .addToBackStack(null)
                                                .commit()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C5CE7))
                                ) {
                                    Text("채팅방으로 이동", color = Color.White)
                                }
                            }
                        }

                        is SubmitState.Success -> {
                            LaunchedEffect(Unit) {
                                android.widget.Toast.makeText(
                                    context,
                                    "신청이 성공적으로 제출되었습니다!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()

                                if (context is FragmentActivity) {
                                    val fragment = FragmentPostChatDetail.newInstance(
                                        chatName = commissionInfo?.artist?.nickname ?: "키르",
                                        authorName = commissionInfo?.title ?: "낙서 타입 커미션",
                                        commissionId = commissionId.toIntOrNull() ?: 1,
                                        hasSubmittedApplication = true
                                    )
                                    context.supportFragmentManager.beginTransaction()
                                        .replace(android.R.id.content, fragment)
                                        .addToBackStack(null)
                                        .commit()
                                }
                                onNavigateToSuccess()
                            }

                            Text(
                                text = "신청이 성공적으로 제출되었습니다!",
                                color = Color.Green,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                        }

                        else -> {}
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (isFormComplete) {
                                viewModel.submitCommissionRequest(
                                    commissionId = commissionId,
                                    formAnswers = formAnswer,
                                    imageUrls = emptyList(),
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
