package com.example.commit.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity

class FragmentCategory : Fragment() {

    companion object {
        fun newInstance(hideBottomBar: Boolean): FragmentCategory {
            return FragmentCategory().apply {
                arguments = Bundle().apply {
                    putBoolean("hideBottomBar", hideBottomBar)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CategoryScreen(
                    onBackClicked = {
                        // 뒤로가기: 이전 화면(Search)로
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    },
                    // FragmentCategory 쪽
                    onCategoryClick = { category ->
                        parentFragmentManager.beginTransaction()
                            .replace(
                                R.id.Nav_Frame,
                                FragmentSearchResult.newInstance(keyword = null, category = category)
                            )
                            .addToBackStack(null)
                            .commit()
                    }

                )
            }
        }
    }

    private fun navigateToSearch() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, FragmentSearch.newInstance(hideBottomBar = true))
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSearchResultByCategory(category: String) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.Nav_Frame,
                // ㄴFragmentSearchResult.newInstance(keyword = null, category = category)
                FragmentSearchResult.newInstance(keyword = null, category = category)
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hideBottomBar = arguments?.getBoolean("hideBottomBar") ?: false
        if (hideBottomBar) {
            (requireActivity() as? MainActivity)?.showBottomNav(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as? MainActivity)?.showBottomNav(true)
    }
}
