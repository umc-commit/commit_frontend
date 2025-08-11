package com.example.commit.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.CommissionFormResponse
import com.example.commit.data.model.CommissionRequestResponse
import com.example.commit.data.model.CommissionRequestSubmit
import com.example.commit.data.model.ImageUploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.webkit.MimeTypeMap
import java.io.File

class CommissionFormViewModel : ViewModel() {

    private val _commissionFormState = MutableStateFlow<CommissionFormState>(CommissionFormState.Loading)
    val commissionFormState: StateFlow<CommissionFormState> = _commissionFormState.asStateFlow()

    private val _imageUploadState = MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    private val _uploadedImageUrls = MutableStateFlow<List<String>>(emptyList())
    val uploadedImageUrls: StateFlow<List<String>> = _uploadedImageUrls.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private var retrofitAPI: RetrofitAPI? = null
    private val gson = Gson()

    /* -------------------------------------------
     * Form API
     * ------------------------------------------- */

    fun getCommissionForm(commissionId: String, context: Context) {
        viewModelScope.launch {
            try {
                _commissionFormState.value = CommissionFormState.Loading

                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                Log.d("CommissionFormViewModel", "토큰 확인: ${if (token.isNullOrEmpty()) "없음" else "있음"}")

                if (token.isNullOrEmpty()) {
                    _commissionFormState.value = CommissionFormState.Error("인증 토큰이 없습니다. 로그인이 필요합니다.")
                    return@launch
                }

                if (retrofitAPI == null) retrofitAPI = RetrofitObject.getRetrofitService(context)

                val response = retrofitAPI!!.getCommissionForm(commissionId)
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("CommissionFormViewModel", "API 응답: $body")
                    _commissionFormState.value = CommissionFormState.Success(body!!)
                } else {
                    _commissionFormState.value = when (response.code()) {
                        401 -> CommissionFormState.Error("인증이 필요합니다. 다시 로그인해주세요.")
                        403 -> CommissionFormState.Error("접근 권한이 없습니다.")
                        404 -> CommissionFormState.Error("커미션을 찾을 수 없습니다.")
                        else -> CommissionFormState.Error("API 호출 실패: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _commissionFormState.value = CommissionFormState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    /* -------------------------------------------
     * Image Upload
     * ------------------------------------------- */

    // 정확한 MIME
    private fun resolveMime(context: Context, uri: Uri): String {
        val fromResolver = context.contentResolver.getType(uri)
        val ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val fromExt = ext?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.lowercase()) }
        val mime = (fromResolver ?: fromExt ?: "image/jpeg")
        return if (mime == "image/jpg") "image/jpeg" else mime
    }

    // 업로드용 파일 생성 (허용 외 포맷은 JPEG 변환)
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

    fun uploadImage(imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                _isUploading.value = true
                _imageUploadState.value = ImageUploadState.Loading

                if (retrofitAPI == null) retrofitAPI = RetrofitObject.getRetrofitService(context)

                val (file, mime) = createUploadFile(context, imageUri)
                val reqBody = file.asRequestBody(mime.toMediaType())

                // Swagger 기준 필드명: "image"
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

    /* -------------------------------------------
     * Submit
     * ------------------------------------------- */

    /**
     * @param answersByLabel UI에서 라벨 -> 값 으로 모은 맵 (예: "신청 내용" -> "귀엽게 그려주세요")
     */
    fun submitCommissionRequest(
        commissionId: String,
        answersByLabel: Map<String, Any>,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                _submitState.value = SubmitState.Loading

                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                if (token.isNullOrEmpty()) {
                    _submitState.value = SubmitState.Error("인증이 필요합니다.")
                    return@launch
                }

                if (retrofitAPI == null) retrofitAPI = RetrofitObject.getRetrofitService(context)

                // 1) 서버 스키마(필드 id/타입) 읽기
                val fields = (commissionFormState.value as? CommissionFormState.Success)
                    ?.data?.success?.formSchema?.fields.orEmpty()

                // 2) 서버가 기대하는 formAnswer (키 = 필드 id)
                val formAnswer = mutableMapOf<String, @JvmSuppressWildcards Any>()

                fields.forEach { f ->
                    val key = f.id.toString()
                    
                    // 디버깅: 각 필드의 값 확인
                    Log.d("SubmitDebug", "필드 처리: id=$key, type=${f.type}, label=${f.label}")
                    
                    when (f.type) {
                        "textarea", "radio", "check" -> {
                            val v = (answersByLabel[f.label] as? String)?.takeIf { it.isNotBlank() }
                            if (v != null) {
                                formAnswer[key] = v
                                Log.d("SubmitDebug", "텍스트 필드 추가: $key = $v")
                            } else {
                                Log.d("SubmitDebug", "텍스트 필드 건너뛰기: $key (빈 값)")
                            }
                        }
                        "file", "image" -> {
                            val urls = uploadedImageUrls.value
                            if (urls.isNotEmpty()) {
                                formAnswer[key] = urls  // 반드시 배열
                                Log.d("SubmitDebug", "이미지 필드 추가: $key = $urls")
                            } else {
                                Log.d("SubmitDebug", "이미지 필드 건너뛰기: $key (빈 배열)")
                            }
                        }
                        else -> {
                            val v = (answersByLabel[f.label] as? String)?.takeIf { it.isNotBlank() }
                            if (v != null) {
                                formAnswer[key] = v
                                Log.d("SubmitDebug", "기타 필드 추가: $key = $v")
                            } else {
                                Log.d("SubmitDebug", "기타 필드 건너뛰기: $key (빈 값)")
                            }
                        }
                    }
                }
                
                // 최종 formAnswer 검증
                Log.d("SubmitDebug", "최종 formAnswer 크기: ${formAnswer.size}")
                formAnswer.forEach { (key, value) ->
                    Log.d("SubmitDebug", "formAnswer[$key] = $value (타입: ${value?.javaClass?.simpleName})")
                }

                // 빈 formAnswer 방지
                if (formAnswer.isEmpty()) {
                    Log.w("SubmitDebug", "formAnswer가 비어있음 - 기본값 추가")
                    formAnswer["1"] = "신청 내용이 없습니다"
                }
                
                // 디버깅 로그
                Log.d("SubmitPayload", gson.toJson(CommissionRequestSubmit(formAnswer = formAnswer)))

                // 3) 요청
                val request = CommissionRequestSubmit(formAnswer = formAnswer)
                val response = retrofitAPI!!.submitCommissionRequest(commissionId, request)

                if (response.isSuccessful) {
                    // 신청서 제출 성공 시 상태 저장
                    markApplicationSubmitted(commissionId, context)
                    _submitState.value = SubmitState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("SubmitError", "서버 응답: ${response.code()}, 에러: $errorBody")

                    _submitState.value = when (response.code()) {
                        400 -> {
                            // 예: C009 expectedType/receivedType 등 서버 메시지 그대로 보여주기
                            SubmitState.Error(errorBody ?: "잘못된 요청입니다.")
                        }
                        401 -> SubmitState.Error("인증이 필요합니다.")
                        404 -> SubmitState.Error("커미션을 찾을 수 없습니다.")
                        500 -> SubmitState.Error("서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.")
                        else -> SubmitState.Error("제출 실패: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _submitState.value = SubmitState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
    
    /* -------------------------------------------
     * Check Application Status
     * ------------------------------------------- */
    
    fun checkApplicationStatus(commissionId: String, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                
                if (token.isNullOrEmpty()) {
                    onResult(false)
                    return@launch
                }
                
                if (retrofitAPI == null) retrofitAPI = RetrofitObject.getRetrofitService(context)
                
                // TODO: 실제로는 신청서 제출 상태를 확인하는 API 엔드포인트가 필요합니다
                // 예: GET /api/commissions/{commissionId}/applications/status
                // 현재는 SharedPreferences에 저장된 신청서 제출 기록을 확인
                val applicationKey = "commission_${commissionId}_submitted"
                val hasSubmitted = prefs.getBoolean(applicationKey, false)
                
                Log.d("CommissionFormViewModel", "신청서 상태 확인: commissionId=$commissionId, hasSubmitted=$hasSubmitted")
                onResult(hasSubmitted)
                
            } catch (e: Exception) {
                Log.e("CommissionFormViewModel", "신청서 상태 확인 실패: ${e.message}")
                onResult(false)
            }
        }
    }
    
    // 신청서 제출 완료 시 상태 저장
    fun markApplicationSubmitted(commissionId: String, context: Context) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val applicationKey = "commission_${commissionId}_submitted"
        prefs.edit().putBoolean(applicationKey, true).apply()
        Log.d("CommissionFormViewModel", "신청서 제출 완료 표시: $commissionId")
    }
}

/* -------------------------------------------
 * States
 * ------------------------------------------- */

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
