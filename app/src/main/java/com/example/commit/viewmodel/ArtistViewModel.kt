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

    // UI에서 수집할 상태
    private val _artistBlock = MutableStateFlow<CommissionArtistResponse?>(null)
    val artistBlock: StateFlow<CommissionArtistResponse?> = _artistBlock

    private val _artistError = MutableStateFlow<String?>(null)
    val artistError: StateFlow<String?> = _artistError

    // 최근 호출 파라미터/진행 중 호출 추적
    private var lastCommissionId: Int? = null
    private var lastPage: Int = 1
    private var lastLimit: Int = 10
    private var inFlight: Call<ApiResponse<CommissionArtistResponse>>? = null

    /**
     * 작가 블록 로드
     * - 동일 (commissionId, page, limit) & 캐시가 존재하면 중복 호출을 스킵
     * - force=true면 무조건 새로 호출
     * - 새로운 요청이 오면 기존 in-flight 콜은 cancel
     */
    fun loadArtist(
        context: Context,
        commissionId: Int,
        page: Int = 1,
        limit: Int = 10,
        force: Boolean = false
    ) {
        // 동일 파라미터 + 캐시 존재 + 강제 새로고침 아님 → 스킵
        if (!force &&
            lastCommissionId == commissionId &&
            lastPage == page &&
            lastLimit == limit &&
            _artistBlock.value != null
        ) {
            Log.d("ArtistViewModel", "skip fetch: cached commissionId=$commissionId")
            return
        }

        _artistError.value = null

        // 진행 중인 콜 취소 (경합 방지)
        inFlight?.cancel()

        val service = RetrofitObject.getRetrofitService(context)
        val call = service.getCommissionArtist(commissionId.toString(), page, limit)
        inFlight = call

        call.enqueue(object : Callback<ApiResponse<CommissionArtistResponse>> {
            override fun onResponse(
                callCb: Call<ApiResponse<CommissionArtistResponse>>,
                response: Response<ApiResponse<CommissionArtistResponse>>
            ) {
                // 오래된 콜이면 무시
                if (inFlight !== callCb) return

                if (!response.isSuccessful) {
                    _artistError.value = "HTTP ${response.code()}"
                    Log.e("ArtistViewModel", "작가 조회 실패 code=${response.code()}")
                    return
                }

                val body = response.body()
                if (body?.resultType == "SUCCESS" && body.success != null) {
                    _artistBlock.value = body.success
                    lastCommissionId = commissionId
                    lastPage = page
                    lastLimit = limit
                    Log.d(
                        "ArtistViewModel",
                        "작가 조회 성공: ${body.success.artist.nickname} / 리뷰 ${body.success.reviewStatistics.totalReviews}개"
                    )
                } else {
                    val reason = body?.error?.reason ?: "알 수 없는 오류"
                    _artistError.value = reason
                    Log.e("ArtistViewModel", "작가 조회 실패: $reason")
                }
            }

            override fun onFailure(
                callCb: Call<ApiResponse<CommissionArtistResponse>>,
                t: Throwable
            ) {
                // 오래된/취소된 콜이면 무시
                if (inFlight !== callCb || callCb.isCanceled) return
                _artistError.value = t.message ?: "네트워크 오류"
                Log.e("ArtistViewModel", "작가 조회 예외", t)
            }
        })
    }

    /**
     * 캐시/에러/진행 중 호출 초기화
     * - 새 커미션 진입 전에 상태를 비우고 싶을 때 사용
     */
    fun reset() {
        inFlight?.cancel()
        inFlight = null
        _artistBlock.value = null
        _artistError.value = null
        lastCommissionId = null
        lastPage = 1
        lastLimit = 10
        Log.d("ArtistViewModel", "reset() 완료")
    }

    override fun onCleared() {
        super.onCleared()
        inFlight?.cancel()
        inFlight = null
    }
}
