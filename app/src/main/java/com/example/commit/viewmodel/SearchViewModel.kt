package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitClient // ApiResponse / Bookmark*Success / BookmarkListSuccess
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.CommissionSummary
import com.example.commit.connection.dto.SearchSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

class SearchViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<CommissionSummary>>(emptyList())
    val results: StateFlow<List<CommissionSummary>> = _results

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // commissionId별 토글 경합 방지(더블탭 방지)
    private val toggleMutexMap = mutableMapOf<Int, Mutex>()
    private fun mutexFor(id: Int): Mutex = toggleMutexMap.getOrPut(id) { Mutex() }

    // commissionId -> bookmarkId 캐시 (검색 응답에 bookmarkId가 없을 때 보완)
    private val bookmarkIdCache = mutableMapOf<Int, Long>()

    // ------------------------------- 검색 -------------------------------
    fun search(
        context: Context,
        rawQuery: String,
        categoryId: Int? = null,
        sortUi: String = "latest",
        minPrice: String? = null,
        maxPrice: String? = null,
        deadlineUi: String = "all",
        followingOnly: Boolean = false,
        selectedTags: List<String> = emptyList(),
        isAuthorMode: Boolean = false,
        page: Int = 1,
        limit: Int = 12
    ) {
        Log.d("SearchViewModel", "search(): q=$rawQuery, categoryId=$categoryId")
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            val keyword = buildKeyword(rawQuery, isAuthorMode, selectedTags).trim()
            if (keyword.isEmpty()) {
                _results.value = emptyList()
                _error.value = "검색어를 입력해주세요."
                _loading.value = false
                return@launch
            }

            val sort = mapSort(sortUi)
            val deadline = mapDeadline(deadlineUi)
            val min = minPrice?.trim()?.toIntOrNull()
            val max = maxPrice?.trim()?.toIntOrNull()

            try {
                val service = RetrofitObject.getRetrofitService(context)
                val resp = service.getSearchResults(
                    keyword = keyword,
                    categoryId = categoryId,
                    sort = sort,
                    minPrice = min,
                    maxPrice = max,
                    deadline = deadline,
                    followingOnly = followingOnly,
                    page = page,
                    limit = limit
                )

                when (resp.resultType) {
                    "SUCCESS" -> {
                        val data: SearchSuccess? = resp.success
                        val list = data?.commissions ?: emptyList()
                        _results.value = list
                        _error.value = null

                        // 검색 응답에 bookmarkId가 있으면 캐시에 미리 심어둠
                        bookmarkIdCache.clear()
                        list.forEach { cs ->
                            cs.bookmarkId?.let { bid -> bookmarkIdCache[cs.id] = bid }
                        }
                    }
                    "FAIL" -> {
                        val reason = resp.error?.reason ?: "검색 실패"
                        _results.value = emptyList()
                        _error.value = reason
                    }
                    else -> {
                        _results.value = emptyList()
                        _error.value = "알 수 없는 응답입니다."
                    }
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val code = e.code()
                    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
                    _error.value = body ?: "요청이 올바르지 않습니다. (HTTP $code)"
                } else {
                    _error.value = e.message ?: "네트워크 오류"
                }
                _results.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    // ----------------------------- 북마크 토글 -----------------------------
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
            Log.d("SearchViewModel", "toggleBookmark(): id=$commissionId, should=$shouldBookmark")

            val beforeList = _results.value
            val current = beforeList.find { it.id == commissionId }

            // 1) 낙관적 선반영 (bookmarkId는 성공 시 반영/정리)
            _results.update { list ->
                list.map { cs ->
                    if (cs.id == commissionId) {
                        if (shouldBookmark) cs.copy(isBookmarked = true)
                        else cs.copy(isBookmarked = false)
                    } else cs
                }
            }

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
                            // 캐시 + 상태에 반영
                            bookmarkIdCache[commissionId] = newBid
                            _results.update { list ->
                                list.map { cs ->
                                    if (cs.id == commissionId) cs.copy(isBookmarked = true, bookmarkId = newBid)
                                    else cs
                                }
                            }
                            _error.value = null
                            Log.d("SearchViewModel", "addBookmark OK: bid=$newBid")
                        } else {
                            val reason = body?.error?.reason ?: "북마크 추가 실패"
                            _results.value = beforeList
                            _error.value = reason
                            Log.w("SearchViewModel", "addBookmark FAIL: $reason")
                        }
                    } else {
                        _results.value = beforeList
                        _error.value = "북마크 추가 실패 (HTTP ${resp.code()})"
                    }
                } else {
                    // 2-B) 삭제: bookmarkId 확보 (현재값 → 캐시 → 서버 목록)
                    val bid: Long? = current?.bookmarkId
                        ?: bookmarkIdCache[commissionId]
                        ?: findBookmarkId(context, commissionId)

                    if (bid == null) {
                        _results.value = beforeList
                        _error.value = "북마크 ID를 찾을 수 없습니다."
                        Log.w("SearchViewModel", "deleteBookmark: bookmarkId=null (commissionId=$commissionId)")
                        return@withLock
                    }

                    val resp = withContext(Dispatchers.IO) {
                        service.deleteBookmark(commissionId.toLong(), bid).execute()
                    }
                    if (resp.isSuccessful) {
                        val body: RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>? = resp.body()
                        if (body?.resultType == "SUCCESS") {
                            // 성공 시 상태/캐시 정리
                            bookmarkIdCache.remove(commissionId)
                            _results.update { list ->
                                list.map { cs ->
                                    if (cs.id == commissionId) cs.copy(isBookmarked = false, bookmarkId = null)
                                    else cs
                                }
                            }
                            _error.value = null
                            Log.d("SearchViewModel", "deleteBookmark OK: bid=$bid")
                        } else {
                            val reason = body?.error?.reason ?: "북마크 삭제 실패"
                            _results.value = beforeList
                            _error.value = reason
                            Log.w("SearchViewModel", "deleteBookmark FAIL: $reason")
                        }
                    } else {
                        _results.value = beforeList
                        _error.value = "북마크 삭제 실패 (HTTP ${resp.code()})"
                    }
                }
            } catch (e: Exception) {
                _results.value = beforeList
                _error.value = e.message ?: "네트워크 오류"
                Log.e("SearchViewModel", "toggleBookmark error", e)
            }
        }
    }

    // ------------------------------ 유틸 ------------------------------

    // 서버의 북마크 목록에서 (commissionId -> bookmarkId) 보충
    private suspend fun findBookmarkId(
        context: Context,
        commissionId: Int
    ): Long? = withContext(Dispatchers.IO) {
        runCatching {
            val service = RetrofitObject.getRetrofitService(context)
            // 페이지 크기는 상황에 맞게. 여기선 넉넉히 100개 조회.
            val resp = service.getBookmarks(page = 1, limit = 100).execute()
            if (!resp.isSuccessful) return@runCatching null
            val body = resp.body()
            val items = body?.success?.items.orEmpty()
            val item = items.firstOrNull { it.id == commissionId && it.bookmarkId != null }
            val bid = item?.bookmarkId
            if (bid != null) {
                bookmarkIdCache[commissionId] = bid
                // 상태에도 반영 (다음 토글에서 바로 사용)
                _results.update { list ->
                    list.map { cs ->
                        if (cs.id == commissionId) cs.copy(isBookmarked = true, bookmarkId = bid) else cs
                    }
                }
            }
            bid
        }.getOrNull()
    }

    private fun buildKeyword(rawQuery: String, isAuthorMode: Boolean, selectedTags: List<String>): String {
        val q = rawQuery.trim()
        return when {
            selectedTags.isNotEmpty() -> selectedTags.joinToString(" ") { "#$it" }
            isAuthorMode -> if (q.startsWith("@")) q else "@$q"
            else -> q
        }
    }

    private fun mapSort(sortUi: String): String = when (sortUi.trim()) {
        "가격낮은순", "price_low" -> "price_low"
        "가격높은순", "price_high" -> "price_high"
        else -> "latest"
    }

    private fun mapDeadline(deadlineUi: String): String = when (deadlineUi.trim()) {
        "1일", "1" -> "1"
        "7일", "7" -> "7"
        "14일", "14" -> "14"
        "30일", "30" -> "30"
        else -> "all"
    }

    @Deprecated("스펙 변경: keyword/categoryId 등으로 교체하세요.")
    fun search(
        context: Context,
        q: String? = null,
        category: String? = null,
        page: Int = 1,
        limit: Int = 12
    ) {
        val raw = q ?: category ?: ""
        search(
            context = context,
            rawQuery = raw,
            categoryId = null,
            sortUi = "latest",
            minPrice = null,
            maxPrice = null,
            deadlineUi = "all",
            followingOnly = false,
            selectedTags = emptyList(),
            isAuthorMode = raw.startsWith("@"),
            page = page,
            limit = limit
        )
    }
}
