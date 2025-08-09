package com.example.commit.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commit.R
import com.example.commit.connection.dto.CommissionSummary
import com.example.commit.ui.post.FragmentPostScreen
import com.example.commit.ui.request.components.Commission
import com.example.commit.viewmodel.SearchViewModel

class FragmentSearchResult : Fragment() {

    companion object {
        private const val ARG_KEYWORD = "arg_keyword"   // 검색창에 보일 텍스트 검색어
        private const val ARG_CATEGORY = "arg_category" // 칩에 보일 카테고리

        fun newInstance(keyword: String?, category: String?): FragmentSearchResult {
            return FragmentSearchResult().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEYWORD, keyword)
                    putString(ARG_CATEGORY, category)
                }
            }
        }
    }

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val initialKeyword = arguments?.getString(ARG_KEYWORD)   // ex) "고양이" (텍스트 검색)
        val initialCategory = arguments?.getString(ARG_CATEGORY) // ex) "그림" (카테고리 검색)

        return ComposeView(requireContext()).apply {
            // FragmentSearchResult.kt (setContent 내부 핵심만)
            setContent {
                // 전달값
                val initialKeyword = arguments?.getString(ARG_KEYWORD)   // 검색어 (검색창에 표시)
                val initialCategory = arguments?.getString(ARG_CATEGORY) // 카테고리 (칩에 표시)
                var showFollowOnly by rememberSaveable { mutableStateOf(false) }

                // 검색창: 키워드 모드면 키워드로 시작, 카테고리 모드면 빈칸
                var searchQuery by rememberSaveable { mutableStateOf(initialKeyword ?: "") }

                // 🔸 FilterButtonRow에 보여줄 라벨: 카테고리 있으면 그 이름, 없으면 "카테고리"
                val categoryLabel = initialCategory ?: "카테고리"

                // ✅ 카테고리로 진입 시 선택된 상태로 보여주기
                var selectedFilters by rememberSaveable {
                    mutableStateOf(
                        if (!initialCategory.isNullOrBlank()) setOf(categoryLabel) else emptySet()
                    )
                }

                // ViewModel 상태 → 카드 리스트 매핑
                val resultsDto by viewModel.results.collectAsState()
                val commissions = resultsDto.map { it.toUi() }

                // 최초 호출: 키워드(q) 우선, 없으면 카테고리(category)
                LaunchedEffect(initialKeyword, initialCategory) {
                    when {
                        !initialKeyword.isNullOrBlank() -> {
                            viewModel.search(requireContext(), q = initialKeyword, category = null, page = 1, limit = 12)
                        }
                        !initialCategory.isNullOrBlank() -> {
                            viewModel.search(requireContext(), q = null, category = initialCategory, page = 1, limit = 12)
                        }
                        else -> requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

                // 검색창 재검색 → 항상 q로
                val submitSearch: () -> Unit = {
                    val q = searchQuery.trim()
                    if (q.isNotEmpty()) {
                        viewModel.search(requireContext(), q = q, category = null, page = 1, limit = 12)
                        // 재검색은 키워드 기반이므로 칩은 선택 해제 유지 (요구사항대로 카테고리만 칩)
                        selectedFilters = emptySet()
                    }
                }

                SearchResultScreen(
                    // 검색창 제어
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchSubmit = submitSearch,

                    // 🔸 FilterButtonRow에 넘길 라벨(항상 카테고리만, 없으면 "카테고리")
                    keyword = categoryLabel,

                    // 🔸 선택 상태: 카테고리 진입이면 setOf(카테고리), 아니면 빈셋
                    selectedFilters = selectedFilters,
                    onFilterClick = { label ->
                        // 토글 (정렬/가격 같은 다른 라벨도 함께 관리)
                        selectedFilters = if (selectedFilters.contains(label)) {
                            selectedFilters - label
                        } else {
                            selectedFilters + label
                        }
                    },
                    onFilterIconClick = { /* TODO: 바텀시트 열기 */ },

                    // 나머지 그대로
                    commissions = commissions,
                    showFollowOnly = showFollowOnly,
                    onFollowToggle = {  checked ->
                        showFollowOnly = checked },
                    onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                    onClearClick = { searchQuery = "" },
                    onHomeClick = { /* TODO */ },
                    onCommissionClick = { item ->
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, FragmentPostScreen.newInstance(item.commissionId))
                            .addToBackStack(null)
                            .commit()
                    }
                )
            }
            }
        }
    }


// DTO → UI 매퍼
private fun CommissionSummary.toUi(): Commission =
    Commission(
        commissionId = id,
        nickname = artist.nickname,
        title = title,
        tags = tags.map { "#${it.name}" }
    )
