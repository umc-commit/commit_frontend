// app/src/main/java/com/example/commit/viewmodel/ArtistViewModel.kt
package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.ApiResponse
import com.example.commit.connection.dto.CommissionArtistResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtistViewModel : ViewModel() {

    private val _artistBlock = MutableStateFlow<CommissionArtistResponse?>(null)
    val artistBlock: StateFlow<CommissionArtistResponse?> = _artistBlock

    private val _artistError = MutableStateFlow<String?>(null)
    val artistError: StateFlow<String?> = _artistError

    fun loadArtist(context: Context, commissionId: Int, page: Int = 1, limit: Int = 10) {
        _artistError.value = null

        val service = RetrofitObject.getRetrofitService(context)
        service.getCommissionArtist(commissionId, page, limit)
            .enqueue(object : Callback<ApiResponse<CommissionArtistResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<CommissionArtistResponse>>,
                    response: Response<ApiResponse<CommissionArtistResponse>>
                ) {
                    if (!response.isSuccessful) {
                        _artistError.value = "HTTP ${response.code()}"
                        Log.e("ArtistViewModel", "작가 조회 실패 code=${response.code()}")
                        return
                    }
                    val body = response.body()
                    if (body?.resultType == "SUCCESS" && body.success != null) {
                        _artistBlock.value = body.success
                        Log.d("ArtistViewModel",
                            "작가 조회 성공: ${body.success.artist.nickname} / 리뷰 ${body.success.reviewStatistics.totalReviews}개")
                    } else {
                        val reason = body?.error?.reason ?: "알 수 없는 오류"
                        _artistError.value = reason
                        Log.e("ArtistViewModel", "작가 조회 실패: $reason")
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<CommissionArtistResponse>>,
                    t: Throwable
                ) {
                    _artistError.value = t.message ?: "네트워크 오류"
                    Log.e("ArtistViewModel", "작가 조회 예외", t)
                }
            })
    }
}
