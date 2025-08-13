package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.ApiResponse
import com.example.commit.connection.dto.RequestItem
import com.example.commit.connection.dto.RequestListResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RequestViewModel : ViewModel() {

    companion object {
        private const val TAG = "RequestViewModel"
    }

    private val _requestList = MutableStateFlow<List<RequestItem>>(emptyList())
    val requestList: StateFlow<List<RequestItem>> = _requestList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadRequests(
        context: Context,
        page: Int = 2, // 기본값 page=2
        append: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "요청 시작: page=$page, append=$append (limit 없음, 토큰은 인터셉터로 자동 추가)")

                val service = RetrofitObject.getRetrofitService(context)
                // limit을 null로 줌 → Retrofit이 파라미터를 안 붙임
                val response: ApiResponse<RequestListResponse> = service.getRequestList(page, null)

                Log.d(TAG, "전체 응답 = $response")

                if (response.resultType == "SUCCESS" && response.success != null) {
                    val newItems = response.success.requests
                    Log.d(TAG, "신청 목록 ${newItems.size}개 로드됨 (page=$page)")

                    _requestList.value =
                        if (append && page > 1) {
                            _requestList.value + newItems
                        } else {
                            newItems
                        }
                } else {
                    val error = response.error?.reason ?: "알 수 없는 오류"
                    Log.e(TAG, "API 실패: $error")
                    _errorMessage.value = error
                }

            } catch (e: Exception) {
                Log.e(TAG, "API 호출 실패", e)
                _errorMessage.value = "네트워크 오류: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
