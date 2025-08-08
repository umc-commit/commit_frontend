package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.ApiResponse
import com.example.commit.connection.dto.RequestDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestDetailViewModel : ViewModel() {

    private val _requestDetail = MutableStateFlow<RequestDetailResponse?>(null)
    val requestDetail: StateFlow<RequestDetailResponse?> = _requestDetail

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadRequestDetail(context: Context, requestId: Int) {
        Log.d("RequestDetailVM", "loadRequestDetail 호출됨: requestId=$requestId")

        viewModelScope.launch {
            try {
                val service = RetrofitObject.getRetrofitService(context)
                val response: ApiResponse<RequestDetailResponse> = service.getRequestDetail(requestId)
                Log.d("RequestDetailVM", "response.success = ${response.success}")

                if (response.resultType == "SUCCESS" && response.success != null) {
                    _requestDetail.value = response.success
                    Log.d("RequestDetailVM", "신청 상세 정보 로드 성공")
                } else {
                    val error = response.error?.reason ?: "알 수 없는 오류"
                    Log.e("RequestDetailVM", "API 실패: $error")
                    _errorMessage.value = error
                }

            } catch (e: Exception) {
                Log.e("RequestDetailVM", "API 호출 실패", e)
                _errorMessage.value = "네트워크 오류: ${e.localizedMessage}"
            }
        }
    }
}
