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

    /** 검색 호출 */
    fun search(
        context: Context,
        q: String? = null,
        category: String? = null,
        page: Int = 1,
        limit: Int = 12
    ) {
        Log.d("SearchViewModel", "search() 호출됨: raw q=$q, category=$category, page=$page, limit=$limit")
        _loading.value = true
        _error.value = null

        viewModelScope.launch {
            // 서버 제약: q는 1글자 이상 → 없으면 category로 대체, 둘 다 없으면 중단
            val normalizedQ = when {
                !q.isNullOrBlank() -> q.trim()
                !category.isNullOrBlank() -> category.trim()
                else -> null
            }
            if (normalizedQ == null) {
                Log.w("SearchViewModel", "q/category 모두 비어 호출 중단")
                _results.value = emptyList()
                _error.value = "검색어를 입력해주세요."
                _loading.value = false
                return@launch
            }

            try {
                val service = RetrofitObject.getRetrofitService(context)
                Log.d("SearchViewModel", "Retrofit 서비스 생성 완료")

                val resp = service.getSearchResults(
                    q = normalizedQ,
                    category = category,
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

                    // 서버가 에러 코드는 body에만 내려주므로 여기서만 매핑
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
}
