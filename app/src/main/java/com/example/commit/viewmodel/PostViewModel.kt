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
        val service = RetrofitObject.getRetrofitService(context)
        service.getCommissionDetail(id).enqueue(object : retrofit2.Callback<com.example.commit.connection.dto.CommissionDetailResponse> {
            override fun onResponse(
                call: retrofit2.Call<com.example.commit.connection.dto.CommissionDetailResponse>,
                response: retrofit2.Response<com.example.commit.connection.dto.CommissionDetailResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.resultType == "SUCCESS") {
                        _commissionDetail.value = responseBody.success
                        Log.d("PostViewModel", "API 응답 성공: ${responseBody.success?.title}")
                    } else {
                        Log.e("PostViewModel", "API 실패: ${responseBody?.resultType}")
                    }
                } else {
                    Log.e("PostViewModel", "API 호출 실패: ${response.code()}")
                }
            }

            override fun onFailure(
                call: retrofit2.Call<com.example.commit.connection.dto.CommissionDetailResponse>,
                t: Throwable
            ) {
                Log.e("PostViewModel", "API 호출 중 예외 발생", t)
            }
        })
    }
}
