package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.CommissionDetail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {

    private val _commissionDetail = MutableStateFlow<CommissionDetail?>(null)
    val commissionDetail: StateFlow<CommissionDetail?> = _commissionDetail

    fun loadCommissionDetail(context: Context, id: Int) {
        viewModelScope.launch {
            try {
                val service = RetrofitObject.getRetrofitService(context)
                val response = service.getCommissionDetail(id)

                if (response.resultType == "SUCCESS") {
                    _commissionDetail.value = response.success
                    Log.d("PostViewModel", "API 응답 성공: ${response.success?.title}")
                } else {
                    Log.e("PostViewModel", "API 실패: ${response.resultType}")
                }
            } catch (e: Exception) {
                Log.e("PostViewModel", "API 호출 중 예외 발생", e)
            }
        }
    }
}
