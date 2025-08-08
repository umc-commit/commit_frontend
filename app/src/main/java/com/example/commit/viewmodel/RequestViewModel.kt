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

    private val _requestList = MutableStateFlow<List<RequestItem>>(emptyList())
    val requestList: StateFlow<List<RequestItem>> = _requestList

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadRequests(context: Context) {
        viewModelScope.launch {
            try {
                val service = RetrofitObject.getRetrofitService(context)
                val response: ApiResponse<RequestListResponse> = service.getRequestList()

                if (response.resultType == "SUCCESS" && response.success != null) {
                    _requestList.value = response.success.requests
                    Log.d("RequestViewModel", "신청 목록 ${response.success.requests.size}개 로드됨")
                } else {
                    val error = response.error?.reason ?: "알 수 없는 오류"
                    Log.e("RequestViewModel", "API 실패: $error")
                    _errorMessage.value = error
                }

            } catch (e: Exception) {
                Log.e("RequestViewModel", "API 호출 실패", e)
                _errorMessage.value = "네트워크 오류: ${e.localizedMessage}"
            }
        }
    }
}
