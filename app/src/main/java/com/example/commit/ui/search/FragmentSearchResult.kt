package com.example.commit.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment

class FragmentSearchResult : Fragment() {

    companion object {
        private const val ARG_KEYWORD = "keyword"
        fun newInstance(keyword: String): FragmentSearchResult {
            return FragmentSearchResult().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEYWORD, keyword)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val keyword = arguments?.getString(ARG_KEYWORD) ?: ""

        return ComposeView(requireContext()).apply {
            setContent {
                var selectedFilters by remember { mutableStateOf(setOf(keyword)) }  // keyword 선택된 상태로 진입
                var showFollowOnly by remember { mutableStateOf(false) }

                SearchResultScreen(
                    keyword = keyword,
                    selectedFilters = selectedFilters,
                    showFollowOnly = showFollowOnly,
                    onFilterClick = { label ->
                        selectedFilters = if (selectedFilters.contains(label)) {
                            selectedFilters - label
                        } else {
                            selectedFilters + label
                        }
                    },
                    onFollowToggle = { showFollowOnly = it }
                )
            }

        }
    }
}
