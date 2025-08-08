package com.example.commit.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommissionFormViewModel : ViewModel() {
    
    private val _commissionFormState = MutableStateFlow<CommissionFormState>(CommissionFormState.Loading)
    val commissionFormState: StateFlow<CommissionFormState> = _commissionFormState.asStateFlow()
    
    private val _imageUploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()
    
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()
    
    private var retrofitAPI: RetrofitAPI? = null
    
    fun getCommissionForm(commissionId: String, context: Context) {
        viewModelScope.launch {
            try {
                _commissionFormState.value = CommissionFormState.Loading
                
                // 토큰 확인
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                
                android.util.Log.d("CommissionFormViewModel", "토큰 확인: ${if (token.isNullOrEmpty()) "없음" else "있음"}")
                

                
                if (token.isNullOrEmpty()) {
                    _commissionFormState.value = CommissionFormState.Error("인증 토큰이 없습니다. 로그인이 필요합니다.")
                    return@launch
                }
                
                // RetrofitAPI 인스턴스 생성
                if (retrofitAPI == null) {
                    retrofitAPI = RetrofitObject.getRetrofitService(context)
                }
                
                val response = retrofitAPI!!.getCommissionForm(commissionId)
                
                if (response.isSuccessful) {
                    response.body()?.let { commissionFormResponse ->
                        // API 응답 로그 출력
                        android.util.Log.d("CommissionFormViewModel", "API 응답: $commissionFormResponse")
                        android.util.Log.d("CommissionFormViewModel", "formSchema 타입: ${commissionFormResponse.success?.formSchema?.javaClass}")
                        android.util.Log.d("CommissionFormViewModel", "formSchema 내용: ${commissionFormResponse.success?.formSchema}")
                        
                        _commissionFormState.value = CommissionFormState.Success(commissionFormResponse)
                    } ?: run {
                        _commissionFormState.value = CommissionFormState.Error("응답 데이터가 없습니다.")
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            _commissionFormState.value = CommissionFormState.Error("인증이 필요합니다. 다시 로그인해주세요.")
                        }
                        403 -> {
                            _commissionFormState.value = CommissionFormState.Error("접근 권한이 없습니다.")
                        }
                        404 -> {
                            _commissionFormState.value = CommissionFormState.Error("커미션을 찾을 수 없습니다.")
                        }
                        else -> {
                            _commissionFormState.value = CommissionFormState.Error("API 호출 실패: ${response.code()}")
                        }
                    }
                }
            } catch (e: Exception) {
                _commissionFormState.value = CommissionFormState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
    
    fun uploadImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _imageUploadState.value = ImageUploadState.Loading
                
                // 토큰 확인
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                
                if (token.isNullOrEmpty()) {
                    _imageUploadState.value = ImageUploadState.Error("인증 토큰이 없습니다.")
                    return@launch
                }
                
                // RetrofitAPI 인스턴스 생성
                if (retrofitAPI == null) {
                    retrofitAPI = RetrofitObject.getRetrofitService(context)
                }
                
                // Uri를 File로 변환
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // MultipartBody 생성
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val multipartBody = MultipartBody.Part.createFormData("image", file.name, requestBody)
                
                val response = retrofitAPI!!.uploadImage(multipartBody)
                
                if (response.isSuccessful) {
                    response.body()?.let { imageUploadResponse ->
                        _imageUploadState.value = ImageUploadState.Success(imageUploadResponse)
                    } ?: run {
                        _imageUploadState.value = ImageUploadState.Error("응답 데이터가 없습니다.")
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            _imageUploadState.value = ImageUploadState.Error("인증이 필요합니다.")
                        }
                        413 -> {
                            _imageUploadState.value = ImageUploadState.Error("파일 크기가 너무 큽니다. (최대 5MB)")
                        }
                        else -> {
                            _imageUploadState.value = ImageUploadState.Error("업로드 실패: ${response.code()}")
                        }
                    }
                }
                
                // 임시 파일 삭제
                file.delete()
                
            } catch (e: Exception) {
                _imageUploadState.value = ImageUploadState.Error("업로드 오류: ${e.message}")
            }
        }
    }
    
    fun submitCommissionRequest(
        commissionId: String,
        formAnswers: Map<String, Any>,
        imageUrls: List<String>,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                _submitState.value = SubmitState.Loading
                
                // 토큰 확인
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                
                if (token.isNullOrEmpty()) {
                    _submitState.value = SubmitState.Error("인증 토큰이 없습니다.")
                    return@launch
                }
                
                // RetrofitAPI 인스턴스 생성
                if (retrofitAPI == null) {
                    retrofitAPI = RetrofitObject.getRetrofitService(context)
                }
                
                // 요청 데이터 생성
                val request = CommissionRequestSubmit(
                    form_answers = formAnswers,
                    image_urls = imageUrls
                )
                
                val response = retrofitAPI!!.submitCommissionRequest(commissionId, request)
                
                if (response.isSuccessful) {
                    response.body()?.let { commissionRequestResponse ->
                        _submitState.value = SubmitState.Success(commissionRequestResponse)
                    } ?: run {
                        _submitState.value = SubmitState.Error("응답 데이터가 없습니다.")
                    }
                } else {
                    when (response.code()) {
                        401 -> {
                            _submitState.value = SubmitState.Error("인증이 필요합니다.")
                        }
                        400 -> {
                            _submitState.value = SubmitState.Error("잘못된 요청입니다.")
                        }
                        404 -> {
                            _submitState.value = SubmitState.Error("커미션을 찾을 수 없습니다.")
                        }
                        else -> {
                            _submitState.value = SubmitState.Error("제출 실패: ${response.code()}")
                        }
                    }
                }
                
            } catch (e: Exception) {
                _submitState.value = SubmitState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
}

sealed class CommissionFormState {
    object Loading : CommissionFormState()
    data class Success(val data: CommissionFormResponse) : CommissionFormState()
    data class Error(val message: String) : CommissionFormState()
}

sealed class ImageUploadState {
    object Idle : ImageUploadState()
    object Loading : ImageUploadState()
    data class Success(val data: ImageUploadResponse) : ImageUploadState()
    data class Error(val message: String) : ImageUploadState()
}

sealed class SubmitState {
    object Idle : SubmitState()
    object Loading : SubmitState()
    data class Success(val data: CommissionRequestResponse) : SubmitState()
    data class Error(val message: String) : SubmitState()
} 