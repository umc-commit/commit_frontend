package com.example.commit.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.foundation.layout.*
import com.example.commit.ui.request.components.Commission
import com.example.commit.ui.post.FragmentPostScreen
import com.example.commit.R



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
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                var selectedFilters by remember { mutableStateOf(setOf(keyword)) }
                var showFollowOnly by remember { mutableStateOf(false) }

                val dummyCommissions = List(10) {
                    Commission(
                        nickname = "작가$it",
                        title = "테스트 커미션 $it",
                        tags = listOf("그림", "#예시", "#귀여움")
                    )
                }

                SearchResultScreen(
                    keyword = keyword,
                    commissions = dummyCommissions,
                    selectedFilters = selectedFilters,
                    showFollowOnly = showFollowOnly,
                    onFilterClick = { label ->
                        selectedFilters = if (selectedFilters.contains(label)) {
                            selectedFilters - label
                        } else {
                            selectedFilters + label
                        }
                    },
                    onFollowToggle = { showFollowOnly = it },
                    onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() },
                    onClearClick = {},
                    onHomeClick = {},
                    onCommissionClick = { commission ->
                        navigateToPostDetail(commission)
                    }
                )
            }
        }
    }

    private fun navigateToPostDetail(commission: Commission) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, FragmentPostScreen.newInstance(commission))
            .addToBackStack(null)
            .commit()
    }
}
