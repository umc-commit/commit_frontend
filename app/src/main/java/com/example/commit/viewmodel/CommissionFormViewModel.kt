package com.example.commit.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.CommissionFormResponse
import com.example.commit.data.model.CommissionRequestSubmit
import com.example.commit.data.model.ImageUploadResponse
import com.example.commit.data.model.CommissionRequestResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import android.webkit.MimeTypeMap
import okhttp3.RequestBody
import java.io.File

class CommissionFormViewModel : ViewModel() {
    
    private val _commissionFormState = MutableStateFlow<CommissionFormState>(CommissionFormState.Loading)
    val commissionFormState: StateFlow<CommissionFormState> = _commissionFormState.asStateFlow()
    
    private val _imageUploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()
    
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()
    
    // 이미지 업로드 상태 추가
    private val _uploadedImageUrls = MutableStateFlow<List<String>>(emptyList())
    val uploadedImageUrls = _uploadedImageUrls.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading = _isUploading.asStateFlow()

    private var retrofitAPI: RetrofitAPI? = null

    // 정확한 MIME 판별
    private fun resolveMime(context: Context, uri: Uri): String {
        val cr = context.contentResolver
        val fromResolver = cr.getType(uri)
        val ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val fromExt = ext?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.lowercase()) }
        val mime = (fromResolver ?: fromExt ?: "image/jpeg")
        return if (mime == "image/jpg") "image/jpeg" else mime
    }

    // 업로드용 파일 생성: 허용 타입 외(예: heic/webp)면 JPEG로 변환
    private fun createUploadFile(context: Context, uri: Uri): Pair<File, String> {
        val allowed = setOf("image/jpeg", "image/png")
        val mime = resolveMime(context, uri)

        if (mime in allowed) {
            val ext = if (mime.endsWith("png")) "png" else "jpg"
            val out = File(context.cacheDir, "upload_${System.currentTimeMillis()}.$ext")
            context.contentResolver.openInputStream(uri)!!.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            }
            return out to mime
        }

        // 허용 외 포맷은 비트맵으로 디코딩 후 JPEG로 변환
        val bmp = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val src = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(src)
        } else {
            @Suppress("DEPRECATION")
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
        val outJpg = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        java.io.FileOutputStream(outJpg).use { fos ->
            bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 92, fos)
        }
        return outJpg to "image/jpeg"
    }


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
    
    // 이미지 업로드 함수
    fun uploadImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                _imageUploadState.value = ImageUploadState.Loading

                if (retrofitAPI == null) {
                    retrofitAPI = RetrofitObject.getRetrofitService(context)
                }

                // 파일 + 정확한 MIME 준비
                val (file, mime) = createUploadFile(context, imageUri)

                // 멀티파트 생성: 절대 "image/*" 쓰지 말고 실제 MIME 사용
                val reqBody = file.asRequestBody(mime.toMediaType())

                val part = MultipartBody.Part.createFormData("image", file.name, reqBody)


                val response = retrofitAPI!!.uploadRequestImage(part)

                if (response.isSuccessful) {
                    val body = response.body()
                    val imageUrl = body?.success?.image_url
                    if (imageUrl != null) {
                        _uploadedImageUrls.value = _uploadedImageUrls.value + imageUrl
                        _imageUploadState.value = ImageUploadState.Success(body)
                        Log.d("ImageUpload", "업로드 성공: $imageUrl (mime=$mime, name=${file.name})")
                    } else {
                        _imageUploadState.value = ImageUploadState.Error("이미지 URL이 비어 있습니다.")
                    }
                } else {
                    val err = response.errorBody()?.string()
                    Log.e("ImageUpload", "실패 ${response.code()}, $err")
                    _imageUploadState.value = ImageUploadState.Error("업로드 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ImageUpload", "예외: ${e.message}", e)
                _imageUploadState.value = ImageUploadState.Error("예외: ${e.message}")
            } finally {
                _isUploading.value = false
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
                val formData = formAnswers.filterValues { it != null && !(it is String && it.isBlank()) }.toMutableMap()
                if (uploadedImageUrls.value.isNotEmpty()) {
                    formData["참고 이미지"] = uploadedImageUrls.value
                }


                // 이미지 URL이 있다면 formData에 추가
                if (_uploadedImageUrls.value.isNotEmpty()) {
                    formData["참고 이미지"] = _uploadedImageUrls.value
                }
                
                val request = CommissionRequestSubmit(
                    formData = formData
                )
                
                val response = retrofitAPI!!.submitCommissionRequest(commissionId, request)
                
                if (response.isSuccessful) {
                    response.body()?.let { commissionRequestResponse ->
                        _submitState.value = SubmitState.Success(commissionRequestResponse)
                    } ?: run {
                        _submitState.value = SubmitState.Error("응답 데이터가 없습니다.")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SubmitError", "서버 응답: ${response.code()}, 에러: $errorBody")
                    
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
                        500 -> {
                            _submitState.value = SubmitState.Error("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
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