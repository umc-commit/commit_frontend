package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.CommissionSummary
import com.example.commit.connection.dto.SearchSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SearchViewModel : ViewModel() {

    private val _results = MutableStateFlow<List<CommissionSummary>>(emptyList())
    val results: StateFlow<List<CommissionSummary>> = _results

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * [기능] 검색 실행
     * - 서버 스펙에 맞게 keyword, categoryId, sort, min/maxPrice, deadline, followingOnly 적용
     * - UI 문자열을 API 값으로 매핑(예: "가격낮은순" -> "price_low", "전체" -> "all")
     * - 태그/작가 모드일 때 keyword 규칙("#태그1 #태그2", "@작가명") 반영
     */
    fun search(
        context: Context,
        rawQuery: String,
        categoryId: Int? = null,
        sortUi: String = "latest",          // "최신순"/"가격낮은순"/"가격높은순" 등 UI 문자열
        minPrice: String? = null,           // 입력칸이 문자열일 수 있어 String로 받음
        maxPrice: String? = null,
        deadlineUi: String = "all",         // "전체"/"1일"/"7일"/"14일"/"30일"
        followingOnly: Boolean = false,
        selectedTags: List<String> = emptyList(), // 태그 선택 시 ["캐릭터","일러스트"]
        isAuthorMode: Boolean = false,      // 작가 검색 탭이면 true
        page: Int = 1,
        limit: Int = 12
    ) {
        Log.d(
            "SearchViewModel",
            "search() 호출됨: rawQuery=$rawQuery, categoryId=$categoryId, sortUi=$sortUi, min=$minPrice, max=$maxPrice, deadlineUi=$deadlineUi, followingOnly=$followingOnly, tags=$selectedTags, isAuthor=$isAuthorMode, page=$page, limit=$limit"
        )
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            // 1) keyword 조립
            val keyword = buildKeyword(rawQuery, isAuthorMode, selectedTags).trim()
            if (keyword.isEmpty()) {
                Log.w("SearchViewModel", "keyword 비어 호출 중단")
                _results.value = emptyList()
                _error.value = "검색어를 입력해주세요."
                _loading.value = false
                return@launch
            }

            // 2) UI 값 → API 값 매핑
            val sort = mapSort(sortUi)
            val deadline = mapDeadline(deadlineUi)
            val min = minPrice?.trim()?.toIntOrNull()
            val max = maxPrice?.trim()?.toIntOrNull()

            try {
                val service = RetrofitObject.getRetrofitService(context)
                Log.d("SearchViewModel", "Retrofit 서비스 생성 완료")

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
                        Log.d("SearchViewModel", "검색 성공: 총 ${list.size}건")
                        _results.value = list
                        _error.value = null
                    }
                    "FAIL" -> {
                        val reason = resp.error?.reason ?: "검색 실패"
                        Log.w("SearchViewModel", "검색 실패: $reason")
                        _results.value = emptyList()
                        _error.value = reason
                    }
                    else -> {
                        Log.w("SearchViewModel", "비정상 resultType=${resp.resultType}")
                        _results.value = emptyList()
                        _error.value = "알 수 없는 응답입니다."
                    }
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    val code = e.code()
                    val body = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
                    Log.e("SearchViewModel", "HTTP $code errorBody=$body", e)

                    _error.value = when {
                        body?.contains("\"S001\"") == true -> "검색어는 1글자 이상이어야 합니다."
                        else -> "요청이 올바르지 않습니다. (HTTP $code)"
                    }
                    _results.value = emptyList()
                } else {
                    Log.e("SearchViewModel", "네트워크/기타 오류: ${e.message}", e)
                    _error.value = e.message ?: "네트워크 오류"
                    _results.value = emptyList()
                }
            } finally {
                _loading.value = false
                Log.d("SearchViewModel", "로딩 상태 종료")
            }
        }
    }

    // ---------- 내부 유틸 ----------

    /** [기능] keyword 조립: 일반/작가/태그 규칙 반영 */
    private fun buildKeyword(
        rawQuery: String,
        isAuthorMode: Boolean,
        selectedTags: List<String>
    ): String {
        val q = rawQuery.trim()
        return when {
            // 태그 모드: "#태그1 #태그2"
            selectedTags.isNotEmpty() -> selectedTags.joinToString(" ") { "#$it" }

            // 작가 모드: "@작가명" (이미 @로 시작하면 중복 방지)
            isAuthorMode -> if (q.startsWith("@")) q else "@$q"

            // 일반 모드
            else -> q
        }
    }

    /** [기능] 정렬 문자열 매핑(UI -> API) */
    private fun mapSort(sortUi: String): String = when (sortUi.trim()) {
        "가격낮은순", "price_low" -> "price_low"
        "가격높은순", "price_high" -> "price_high"
        else -> "latest"
    }

    /** [기능] 마감기한 매핑(UI -> API) */
    private fun mapDeadline(deadlineUi: String): String = when (deadlineUi.trim()) {
        "1일", "1" -> "1"
        "7일", "7" -> "7"
        "14일", "14" -> "14"
        "30일", "30" -> "30"
        else -> "all"
    }

    // ---------- (선택) 구버전 호환 오버로드 ----------
    /**
     * [기능] 구버전 호환: q/category 문자열 기반 호출 → 스펙에 맞춰 변환하여 위 메서드로 위임
     * - 기존 호출부가 많을 경우 임시로 사용하고, 점진적으로 제거 권장
     */
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
            categoryId = null,            // 문자열 category를 더이상 사용하지 않음
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
