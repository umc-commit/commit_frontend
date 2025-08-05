package com.example.commit.ui.request.form

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.R
import com.example.commit.data.model.*
import com.example.commit.fragment.FragmentFormCheckScreen
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.viewmodel.CommissionFormState
import com.example.commit.viewmodel.CommissionFormViewModel
import com.example.commit.viewmodel.ImageUploadState
import com.example.commit.viewmodel.SubmitState
import com.google.gson.Gson

@Composable
fun CommissionFormScreen(commissionId: String = "1") {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val viewModel: CommissionFormViewModel = viewModel()
    
    // API 호출
    LaunchedEffect(commissionId) {
        viewModel.getCommissionForm(commissionId, context)
    }
    
    val commissionFormState by viewModel.commissionFormState.collectAsState()
    val imageUploadState by viewModel.imageUploadState.collectAsState()
    val submitState by viewModel.submitState.collectAsState()
    
    // 기본 폼 스키마 (API 응답이 없을 때 사용)
    val defaultFormSchema = listOf(
        FormItem(
            label = "당일마감 옵션",
            type = "radio",
            options = listOf(OptionItem("O (+10000P)"), OptionItem("X"))
        ),
        FormItem(
            label = "신청 캐릭터",
            type = "radio",
            options = listOf(OptionItem("고양이"), OptionItem("햄스터"), OptionItem("캐리커쳐"), OptionItem("랜덤"))
        ),
        FormItem(
            label = "확인 여부",
            type = "check",
            options = listOf(OptionItem("확인했습니다."))
        ),
        FormItem(
            label = "이미지",
            type = "image"
        )
    )
    
    // API 응답에서 폼 스키마를 가져오거나 기본값 사용
    val formSchema = when (commissionFormState) {
        is CommissionFormState.Success -> {
            val response = (commissionFormState as CommissionFormState.Success).data
            response.success?.formSchema ?: defaultFormSchema
        }
        else -> defaultFormSchema
    }

    val formAnswer = remember { mutableStateMapOf<String, Any>() }
    val images = remember { mutableStateListOf<Bitmap>() }

    val isFormComplete by remember {
        derivedStateOf {
            val requiredFields = listOf("당일마감 옵션", "신청 캐릭터", "확인 여부")
            Log.d("FormDebug", "=== 폼 상태 체크 시작 ===")
            Log.d("FormDebug", "전체 formAnswer: $formAnswer")
            
            val completedFields = requiredFields.count { field ->
                val value = formAnswer[field]
                val isValid = when (field) {
                    "확인 여부" -> value == "확인했습니다."
                    else -> (value as? String)?.isNotBlank() == true
                }
                Log.d("FormDebug", "필드: $field, 값: '$value', 유효성: $isValid")
                isValid
            }
            val result = completedFields >= 1
            Log.d("FormDebug", "완료된 필드 수: $completedFields, 최종 결과: $result")
            Log.d("FormDebug", "=== 폼 상태 체크 끝 ===")
            result
        }
    }

    when (commissionFormState) {
        is CommissionFormState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is CommissionFormState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "오류가 발생했습니다",
                        fontSize = 16.sp,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (commissionFormState as CommissionFormState.Error).message,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.getCommissionForm(commissionId, context) }
                    ) {
                        Text("다시 시도")
                    }
                }
            }
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                CommissionTopBar(
                    onBackClick = {
                        // 뒤로가기 기능
                        Log.d("FormDebug", "뒤로가기 버튼 클릭됨")
                        if (context is FragmentActivity) {
                            Log.d("FormDebug", "context가 FragmentActivity입니다. popBackStack 호출")
                            context.supportFragmentManager.popBackStack()
                        } else {
                            Log.d("FormDebug", "context가 FragmentActivity가 아닙니다: ${context.javaClass.simpleName}")
                            // CommissionFormActivity의 경우 finish() 호출
                            if (context is androidx.activity.ComponentActivity) {
                                Log.d("FormDebug", "ComponentActivity의 finish() 호출")
                                context.finish()
                            }
                        }
                    }
                )
                
                // API 응답에서 커미션 정보 가져오기
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
                Spacer(modifier = Modifier.height(20.dp))
                Divider(thickness = 8.dp, color = Color(0xFFD9D9D9))
                Spacer(modifier = Modifier.height(20.dp))

        // 폼 내용을 감싸는 Column (패딩 적용)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            formSchema.forEachIndexed { index, item ->
                Spacer(modifier = Modifier.height(12.dp))
                when (item.type) {
                    "radio", "check" -> {
                        val selectedOption = formAnswer[item.label] as? String ?: ""
                        CommissionOptionSection(
                            index = index + 1,
                            title = item.label,
                            options = item.options.map { it.label },
                            selectedOption = selectedOption,
                            onOptionSelected = { 
                                Log.d("FormDebug", "옵션 선택됨 - 필드: ${item.label}, 선택된 값: $it")
                                formAnswer[item.label] = it
                                Log.d("FormDebug", "formAnswer 업데이트 후: $formAnswer")
                            }
                        )
                    }
                    "image" -> {
                        // 이미지 업로드 상태 처리
                        when (imageUploadState) {
                            is ImageUploadState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
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
                        
                        CommissionImageTextSection(
                            text = formAnswer["신청 내용"] as? String ?: "",
                            onTextChange = { 
                                formAnswer["신청 내용"] = it
                                Log.d("FormDebug", "Image text changed: $it")
                            },
                            images = images,
                            onAddClick = { /* TODO */ },
                            onRemoveClick = { index -> images.removeAt(index) },
                            onImageUpload = { uri ->
                                viewModel.uploadImage(uri, context)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            
            // 제출 상태 처리
            when (submitState) {
                is SubmitState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is SubmitState.Error -> {
                    Text(
                        text = (submitState as SubmitState.Error).message,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                is SubmitState.Success -> {
                    Text(
                        text = "신청이 성공적으로 제출되었습니다!",
                        color = Color.Green,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                else -> {}
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (isFormComplete) {
                        // 제출 로직
                        val imageUrls = mutableListOf<String>()
                        // TODO: 업로드된 이미지 URL들을 imageUrls에 추가
                        
                        viewModel.submitCommissionRequest(
                            commissionId = commissionId,
                            formAnswers = formAnswer,
                            imageUrls = imageUrls,
                            context = context
                        )
                        
                        // 기존 로직 (임시로 유지)
                        val gson = Gson()
                        val schemaJson = gson.toJson(formSchema)
                        val answerJson = gson.toJson(formAnswer)

                        val requestItem = RequestItem(
                            requestId = 1,
                            status = "진행중",
                            title = "낙서 타입 커미션",
                            price = 10000,
                            thumbnailImage = "https://example.com/image.jpg",
                            artist = Artist(id = 101, nickname = "키르"),
                            createdAt = "2023.2.3 19:20"
                        )
                        val requestItemJson = gson.toJson(requestItem)

                        val fragment = FragmentFormCheckScreen().apply {
                            arguments = Bundle().apply {
                                putString("requestItem", requestItemJson)
                                putString("formSchema", schemaJson)
                                putString("formAnswer", answerJson)
                            }
                        }

                        (context as FragmentActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, fragment)
                            .addToBackStack(null)
                            .commit()
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

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun CommissionFormScreenPreview() {
    CommitTheme {
        CommissionFormScreen()
    }
}
