package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitClient // ApiResponse / Bookmark*Success / BookmarkListSuccess
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.CommissionDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PostViewModel : ViewModel() {

    private val _commissionDetail = MutableStateFlow<CommissionDetail?>(null)
    val commissionDetail: StateFlow<CommissionDetail?> = _commissionDetail

    // 홈에서 받은 작가 정보 캐시 (기존)
    private val _artistId = MutableStateFlow<Int?>(null)
    val artistId: StateFlow<Int?> = _artistId
    private val _artistName = MutableStateFlow<String?>(null)
    val artistName: StateFlow<String?> = _artistName

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

                            // 검색화면과 동일: 상세 응답에 bookmarkId가 오면 캐시에 저장
                            val cd = body.success
                            if (cd != null) {
                                cd.bookmarkId?.let { bid -> bookmarkIdCache[cd.id] = bid }
                            }
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

    // ----------------------------- 북마크 토글 -----------------------------

    // 상세도 검색과 동일한 패턴 적용: 더블탭 방지 + bookmarkId 캐시 + fallback 조회
    private val toggleMutexMap = mutableMapOf<Int, Mutex>()
    private fun mutexFor(id: Int): Mutex = toggleMutexMap.getOrPut(id) { Mutex() }

    // commissionId -> bookmarkId 캐시
    private val bookmarkIdCache = mutableMapOf<Int, Long>()

    /**
     * shouldBookmark = true  -> POST /api/commissions/{commissionId}/bookmarks
     * shouldBookmark = false -> DELETE /api/commissions/{commissionId}/bookmarks/{bookmarkId}
     * 응답 success: { bookmarkId, commissionId, message }
     */
    fun toggleBookmark(
        context: Context,
        commissionId: Int,
        shouldBookmark: Boolean
    ) = viewModelScope.launch {
        mutexFor(commissionId).withLock {
            val before = _commissionDetail.value
            Log.d("PostViewModel", "toggleBookmark(): id=$commissionId, should=$shouldBookmark, before.isBookmarked=${before?.isBookmarked}, before.bookmarkId=${before?.bookmarkId}")

            // 1) 낙관적 선반영 (bookmarkId는 성공 시 반영/정리)
            _commissionDetail.update { it?.copy(isBookmarked = shouldBookmark) }

            try {
                val service = RetrofitObject.getRetrofitService(context)

                if (shouldBookmark) {
                    // 2-A) 추가
                    val resp = withContext(Dispatchers.IO) {
                        service.addBookmark(commissionId.toLong()).execute()
                    }
                    if (resp.isSuccessful) {
                        val body: RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>? = resp.body()
                        val success = body?.success
                        if (body?.resultType == "SUCCESS" && success != null) {
                            val newBid = success.bookmarkId
                            bookmarkIdCache[commissionId] = newBid
                            // 상세에 bookmarkId 필드가 있다면 함께 반영
                            _commissionDetail.update { cur ->
                                cur?.copy(isBookmarked = true, bookmarkId = newBid)
                                    ?: cur
                            }
                            Log.d("PostViewModel", "addBookmark OK: bid=$newBid")
                        } else {
                            val reason = body?.error?.reason ?: "북마크 추가 실패"
                            _commissionDetail.value = before // 롤백
                            Log.w("PostViewModel", "addBookmark FAIL: $reason")
                        }
                    } else {
                        _commissionDetail.value = before // 롤백
                        Log.w("PostViewModel", "addBookmark HTTP ${resp.code()}")
                    }
                } else {
                    // 2-B) 삭제: bookmarkId 확보 (상세값 → 캐시 → 목록조회)
                    val currentBid: Long? =
                        _commissionDetail.value?.bookmarkId
                            ?: bookmarkIdCache[commissionId]
                            ?: findBookmarkId(context, commissionId)

                    if (currentBid == null) {
                        _commissionDetail.value = before // 롤백
                        Log.w("PostViewModel", "deleteBookmark: bookmarkId=null (id=$commissionId)")
                        return@withLock
                    }

                    val resp = withContext(Dispatchers.IO) {
                        service.deleteBookmark(commissionId.toLong(), currentBid).execute()
                    }
                    if (resp.isSuccessful) {
                        val body: RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>? = resp.body()
                        if (body?.resultType == "SUCCESS") {
                            bookmarkIdCache.remove(commissionId)
                            // 성공 시에만 bookmarkId를 null로 (상세에 필드가 있을 때)
                            _commissionDetail.update { cur ->
                                cur?.copy(isBookmarked = false, bookmarkId = null)
                                    ?: cur?.copy(isBookmarked = false)
                            }
                            Log.d("PostViewModel", "deleteBookmark OK: bid=$currentBid")
                        } else {
                            val reason = body?.error?.reason ?: "북마크 삭제 실패"
                            _commissionDetail.value = before // 롤백
                            Log.w("PostViewModel", "deleteBookmark FAIL: $reason")
                        }
                    } else {
                        _commissionDetail.value = before // 롤백
                        Log.w("PostViewModel", "deleteBookmark HTTP ${resp.code()}")
                    }
                }
            } catch (e: Exception) {
                _commissionDetail.value = before // 예외 시 롤백
                Log.e("PostViewModel", "toggleBookmark error", e)
            }
        }
    }

    // 서버 북마크 목록에서 (commissionId -> bookmarkId) 보충
    private suspend fun findBookmarkId(
        context: Context,
        commissionId: Int
    ): Long? = withContext(Dispatchers.IO) {
        runCatching {
            val service = RetrofitObject.getRetrofitService(context)
            val resp = service.getBookmarks(page = 1, limit = 100).execute()
            if (!resp.isSuccessful) return@runCatching null
            val body = resp.body()
            val items = body?.success?.items.orEmpty()
            val item = items.firstOrNull { it.id == commissionId && it.bookmarkId != null }
            val bid = item?.bookmarkId
            if (bid != null) {
                bookmarkIdCache[commissionId] = bid
                // 상세에도 반영 (다음 토글 편의)
                _commissionDetail.update { cur ->
                    cur?.copy(isBookmarked = true, bookmarkId = bid) ?: cur
                }
            }
            bid
        }.getOrNull()
    }
}
