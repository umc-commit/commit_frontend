package com.example.commit.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.CommissionFormResponse
import com.example.commit.data.model.CommissionRequestSubmit
import com.example.commit.data.model.ImageUploadResponse
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.webkit.MimeTypeMap
import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommissionFormViewModel : ViewModel() {

    private val _commissionFormState =
        MutableStateFlow<CommissionFormState>(CommissionFormState.Loading)
    val commissionFormState: StateFlow<CommissionFormState> = _commissionFormState.asStateFlow()

    private val _imageUploadState =
        MutableStateFlow<ImageUploadState>(ImageUploadState.Idle)
    val imageUploadState: StateFlow<ImageUploadState> = _imageUploadState.asStateFlow()

    private val _submitState =
        MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()

    private val _uploadedImageUrls = MutableStateFlow<List<String>>(emptyList())
    val uploadedImageUrls: StateFlow<List<String>> = _uploadedImageUrls.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    // ▼ 제출된 신청서 보기 상태
    private val _submittedFormState =
        MutableStateFlow<SubmittedFormState>(SubmittedFormState.Idle)
    val submittedFormState: StateFlow<SubmittedFormState> = _submittedFormState.asStateFlow()

    private val _submittedFormSchemaUi =
        MutableStateFlow<List<FormItem>>(emptyList())
    val submittedFormSchemaUi: StateFlow<List<FormItem>> = _submittedFormSchemaUi.asStateFlow()

    private var retrofitAPI: RetrofitAPI? = null
    private val gson = Gson()

    // ---------------------------------------------------------------------
    // Form 작성용 스키마 조회
    // ---------------------------------------------------------------------
    fun getCommissionForm(commissionId: String, context: Context) {
        viewModelScope.launch {
            try {
                _commissionFormState.value = CommissionFormState.Loading

                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                Log.d("CommissionFormViewModel", "토큰 확인: ${if (token.isNullOrEmpty()) "없음" else "있음"}")

                if (token.isNullOrEmpty()) {
                    _commissionFormState.value =
                        CommissionFormState.Error("인증 토큰이 없습니다. 로그인이 필요합니다.")
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

    // ---------------------------------------------------------------------
    // 이미지 업로드
    // ---------------------------------------------------------------------
    private fun resolveMime(context: Context, uri: Uri): String {
        val fromResolver = context.contentResolver.getType(uri)
        val ext = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        val fromExt = ext?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.lowercase()) }
        val mime = (fromResolver ?: fromExt ?: "image/jpeg")
        return if (mime == "image/jpg") "image/jpeg" else mime
    }

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

    // ---------------------------------------------------------------------
    // 신청서 제출
    // ---------------------------------------------------------------------
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

                val fields = (commissionFormState.value as? CommissionFormState.Success)
                    ?.data?.success?.formSchema?.fields.orEmpty()

                val formAnswer = mutableMapOf<String, @JvmSuppressWildcards Any>()
                fields.forEach { f ->
                    val key = f.id.toString()
                    when (f.type) {
                        "textarea", "radio", "check" -> {
                            val v = (answersByLabel[f.label] as? String)?.takeIf { it.isNotBlank() }
                            if (v != null) formAnswer[key] = v
                        }
                        "file", "image" -> {
                            val urls = uploadedImageUrls.value
                            if (urls.isNotEmpty()) formAnswer[key] = urls
                        }
                        else -> {
                            val v = (answersByLabel[f.label] as? String)?.takeIf { it.isNotBlank() }
                            if (v != null) formAnswer[key] = v
                        }
                    }
                }
                if (formAnswer.isEmpty()) formAnswer["1"] = "신청 내용이 없습니다"

                val request = CommissionRequestSubmit(formAnswer = formAnswer)
                Log.d("SubmitPayload", gson.toJson(request))

                val response = retrofitAPI!!.submitCommissionRequest(commissionId, request)
                if (response.isSuccessful) {
                    markApplicationSubmitted(commissionId, context)
                    val requestId = response.body()?.success?.request_id?.toString()
                    _submitState.value = SubmitState.Success(requestId)
                } else {
                    val errorBody = response.errorBody()?.string().orEmpty()
                    Log.e("SubmitError", "서버 응답: ${response.code()}, 에러: $errorBody")
                    _submitState.value = when (response.code()) {
                        400 -> {
                            if (errorBody.contains("C010") || errorBody.contains("이미 신청한 커미션입니다")) {
                                val existingId = Regex("\"existingRequestId\"\\s*:\\s*\"?(\\d+)\"?")
                                    .find(errorBody)?.groupValues?.getOrNull(1).orEmpty()
                                markApplicationSubmitted(commissionId, context)
                                if (existingId.isNotBlank()) {
                                    SubmitState.AlreadySubmitted(existingId)
                                } else {
                                    SubmitState.Error("이미 신청한 커미션입니다")
                                }
                            } else {
                                SubmitState.Error(if (errorBody.isNotEmpty()) errorBody else "잘못된 요청입니다.")
                            }
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

    // ---------------------------------------------------------------------
    // 제출 여부 확인 (로컬 플래그)
    // ---------------------------------------------------------------------
    fun checkApplicationStatus(
        commissionId: String,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)

                if (token.isNullOrEmpty()) {
                    onResult(false); return@launch
                }
                if (retrofitAPI == null) retrofitAPI = RetrofitObject.getRetrofitService(context)

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

    fun markApplicationSubmitted(commissionId: String, context: Context) {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val applicationKey = "commission_${commissionId}_submitted"
        prefs.edit().putBoolean(applicationKey, true).apply()
        Log.d("CommissionFormViewModel", "신청서 제출 완료 표시: $commissionId")
    }

    companion object {
        private const val TAG = "CommissionFormVM"
    }

    // ---------------------------------------------------------------------
    // 제출된 신청서 보기 (Call<...> → enqueue 사용)  ── ★ 로그 추가 버전
    // ---------------------------------------------------------------------
    fun getSubmittedCommissionForm(commissionId: String, context: Context) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "[getSubmittedCommissionForm] enter, commissionId=$commissionId")
                _submittedFormState.value = SubmittedFormState.Loading

                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs.getString("accessToken", null)
                Log.d(
                    TAG,
                    "tokenPresent=${!token.isNullOrEmpty()} tokenPreview=${token?.take(10)}..."
                )
                if (token.isNullOrEmpty()) {
                    Log.w(TAG, "no token → abort")
                    _submittedFormState.value =
                        SubmittedFormState.Error("인증 토큰이 없습니다. 로그인이 필요합니다.")
                    return@launch
                }

                if (retrofitAPI == null) {
                    retrofitAPI = RetrofitObject.getRetrofitService(context)
                    Log.d(TAG, "Retrofit service initialized: ${retrofitAPI != null}")
                }

                val call = retrofitAPI!!.getSubmittedCommissionForm(commissionId.toInt())
                // call.request()는 OkHttp 4.x 기준 동기 생성 가능
                runCatching { Log.d(TAG, "enqueue url=${call.request().url}") }
                    .onFailure { Log.w(TAG, "request.url read failed: ${it.message}") }

                call.enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>> {

                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>,
                        response: Response<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>
                    ) {
                        Log.d(
                            TAG,
                            "onResponse code=${response.code()} isSuccessful=${response.isSuccessful}"
                        )

                        if (!response.isSuccessful) {
                            val msg = when (response.code()) {
                                401 -> "인증이 필요합니다. 다시 로그인해주세요."
                                403 -> "접근 권한이 없습니다."
                                404 -> "리소스를 찾을 수 없습니다."
                                else -> "API 호출 실패: ${response.code()}"
                            }
                            Log.w(TAG, "httpError: $msg")
                            _submittedFormState.value = SubmittedFormState.Error(msg)
                            return
                        }

                        val body = response.body()
                        Log.d(
                            TAG,
                            "body null=${body == null} resultType=${body?.resultType} hasSuccess=${body?.success != null}"
                        )

                        val ok = body?.resultType == "SUCCESS" && body.success != null
                        if (!ok) {
                            Log.w(TAG, "logical error: resultType!=SUCCESS or success==null")
                            _submittedFormState.value =
                                SubmittedFormState.Error("신청서 데이터를 불러오지 못했습니다.")
                            return
                        }

                        val success = body!!.success!!
                        // success 개략 로그
                        runCatching {
                            val sObj = gson.toJsonTree(success).asJsonObject
                            val commissionIdLog = sObj.getAsJsonObject("commission")?.get("id")
                            val titleLog = sObj.getAsJsonObject("commission")?.get("title")
                            Log.d(TAG, "success.commission.id=$commissionIdLog title=$titleLog")
                        }.onFailure {
                            Log.w(TAG, "success quick log failed: ${it.message}")
                        }

                        _submittedFormState.value = SubmittedFormState.Success(success)

                        // UI 매핑
                        try {
                            val successJson = gson.toJsonTree(success).asJsonObject
                            val fieldsJson = successJson
                                .getAsJsonObject("formSchema")
                                ?.getAsJsonArray("fields")

                            Log.d(
                                TAG,
                                "mapping fieldsJson size=${fieldsJson?.size() ?: 0}"
                            )

                            val mapped = mapSchemaFieldsToFormItems(fieldsJson)
                            _submittedFormSchemaUi.value = mapped

                            Log.d(TAG, "mapped FormItem count=${mapped.size}")
                        } catch (e: Exception) {
                            _submittedFormSchemaUi.value = emptyList()
                            Log.e(TAG, "mapping failed: ${e.message}", e)
                        }
                    }

                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>,
                        t: Throwable
                    ) {
                        Log.e(TAG, "onFailure: ${t.message}", t)
                        _submittedFormState.value =
                            SubmittedFormState.Error("네트워크 오류: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "getSubmittedCommissionForm exception: ${e.message}", e)
                _submittedFormState.value = SubmittedFormState.Error("네트워크 오류: ${e.message}")
            }
        }
    }

    // ---------------------------------------------------------------------
    // success.formSchema.fields(JSON) → UI FormItem 리스트 변환
    // ---------------------------------------------------------------------
    private fun mapSchemaFieldsToFormItems(fieldsJson: JsonArray?): List<FormItem> {
        if (fieldsJson == null || fieldsJson.size() == 0) {
            Log.w(TAG, "mapSchemaFieldsToFormItems: fieldsJson is null/empty")
            return emptyList()
        }

        val result = mutableListOf<FormItem>()
        fieldsJson.forEachIndexed { index, elem ->
            val field = elem.asJsonObject
            val idStr = field.get("id")?.asString ?: "${index + 1}"
            val id = idStr.toIntOrNull() ?: (index + 1)
            val type = field.get("type")?.asString ?: "text"
            val label = field.get("label")?.asString ?: "항목 ${index + 1}"

            val optionsJson = field.get("options") as? JsonArray
            val options: List<OptionItem> =
                if (optionsJson != null && optionsJson.size() > 0) {
                    optionsJson.map { optEl ->
                        val optObj = optEl.asJsonObject
                        val base = optObj.get("label")?.asString ?: ""
                        val addPrice = optObj.get("additionalPrice")?.asInt ?: 0
                        val finalLabel = if (addPrice > 0) "$base (+${addPrice}P)" else base
                        OptionItem(finalLabel)
                    }
                } else emptyList()

            Log.v(
                TAG,
                "map[$index] id=$id type=$type label=$label options=${options.size}"
            )

            result += FormItem(
                id = id,
                label = label,
                type = type,
                options = options
            )
        }
        return result
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

/** 성공 시엔 requestId(String?)만 전달 */
sealed class SubmitState {
    data object Idle : SubmitState()
    data object Loading : SubmitState()
    data class Success(val requestId: String? = null) : SubmitState()
    data class Error(val message: String) : SubmitState()
    data class AlreadySubmitted(val existingRequestId: String) : SubmitState()
}

/** 제출된 신청서 보기용 상태 */
sealed class SubmittedFormState {
    data object Idle : SubmittedFormState()
    data object Loading : SubmittedFormState()
    data class Success(val data: RetrofitClient.SubmittedFormData) : SubmittedFormState()
    data class Error(val message: String) : SubmittedFormState()
}
