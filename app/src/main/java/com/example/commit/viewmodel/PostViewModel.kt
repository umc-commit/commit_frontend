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

    // 홈에서 받은 작가 정보 캐시
    private val _artistId = MutableStateFlow<Int?>(null)
    val artistId: StateFlow<Int?> = _artistId
    private val _artistName = MutableStateFlow<String?>(null)
    val artistName: StateFlow<String?> = _artistName

    // 홈/상세 진입 시 주입
    fun setArtistFromHome(id: Int?, name: String?) {
        _artistId.value = id
        _artistName.value = name
    }

    fun loadCommissionDetail(context: Context, id: Int) {
        val service = RetrofitObject.getRetrofitService(context)
        service.getCommissionDetail(id)
            .enqueue(object : retrofit2.Callback<com.example.commit.connection.dto.CommissionDetailResponse> {
                override fun onResponse(
                    call: retrofit2.Call<com.example.commit.connection.dto.CommissionDetailResponse>,
                    response: retrofit2.Response<com.example.commit.connection.dto.CommissionDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.resultType == "SUCCESS") {
                            _commissionDetail.value = body.success
                            Log.d("PostViewModel", "상세 성공: ${body.success?.title}")
                        } else {
                            Log.e("PostViewModel", "상세 실패: ${body?.resultType}")
                        }
                    } else Log.e("PostViewModel", "상세 실패 code=${response.code()}")
                }
                override fun onFailure(
                    call: retrofit2.Call<com.example.commit.connection.dto.CommissionDetailResponse>,
                    t: Throwable
                ) { Log.e("PostViewModel", "상세 예외", t) }
            })
    }
}
