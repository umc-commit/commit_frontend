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
import androidx.lifecycle.lifecycleScope
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.connection.dto.CommissionSummary
import com.example.commit.ui.post.FragmentPostScreen
import com.example.commit.ui.request.components.Commission
import kotlinx.coroutines.launch
import com.example.commit.viewmodel.SearchViewModel

class FragmentSearchResult : Fragment() {

    companion object {
        private const val ARG_KEYWORD = "arg_keyword"
        private const val ARG_CATEGORY = "arg_category"

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
        return ComposeView(requireContext()).apply {
            setContent {
                val initialKeyword = arguments?.getString(ARG_KEYWORD)
                val initialCategory = arguments?.getString(ARG_CATEGORY)

                var showFollowOnly by rememberSaveable { mutableStateOf(false) }
                var searchQuery by rememberSaveable { mutableStateOf(initialKeyword ?: "") }
                val categoryLabel = initialCategory ?: "카테고리"

                var selectedFilters by rememberSaveable {
                    mutableStateOf(
                        if (!initialCategory.isNullOrBlank()) setOf(categoryLabel) else emptySet()
                    )
                }

                val resultsDto by viewModel.results.collectAsState()
                val commissions = remember(resultsDto) { resultsDto.map { it.toUi() } }

                LaunchedEffect(initialKeyword, initialCategory) {
                    when {
                        !initialKeyword.isNullOrBlank() ->
                            viewModel.search(requireContext(), q = initialKeyword, category = null, page = 1, limit = 12)
                        !initialCategory.isNullOrBlank() ->
                            viewModel.search(requireContext(), q = null, category = initialCategory, page = 1, limit = 12)
                        else -> requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }

                val submitSearch: () -> Unit = {
                    val q = searchQuery.trim()
                    if (q.isNotEmpty()) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.search(requireContext(), q = q, category = null, page = 1, limit = 12)
                        }
                        selectedFilters = emptySet()
                    }
                }

                SearchResultScreen(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onSearchSubmit = submitSearch,
                    keyword = categoryLabel,
                    commissions = commissions,
                    selectedFilters = selectedFilters,
                    showFollowOnly = showFollowOnly,
                    onFilterClick = { label ->
                        selectedFilters = if (selectedFilters.contains(label)) selectedFilters - label else selectedFilters + label
                    },
                    onFilterIconClick = { /* TODO */ },
                    onFollowToggle = { checked -> showFollowOnly = checked },
                    onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                    onClearClick = { searchQuery = "" },
                    onHomeClick = { /* TODO */ },
                    onCommissionClick = { item ->
                        (requireActivity() as? MainActivity)?.showBottomNav(false)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, FragmentPostScreen.newInstance(item.commissionId))
                            .addToBackStack(null)
                            .commit()
                    },
                    onBookmarkToggle = { id, newState ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.toggleBookmark(requireContext(), id, newState)
                        }
                    }
                )
            }
        }
    }
}

/** DTO → UI 매핑 */
private fun CommissionSummary.toUi(): Commission =
    Commission(
        commissionId = id,
        nickname = artist.nickname,
        title = title,
        tags = tags.map { "#${it.name}" },
        thumbnailImageUrl = thumbnailImageUrl ?: "",
        isBookmarked = (isBookmarked ?: false)
    )
