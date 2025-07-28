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
                        navigateToSearch()
                    },
                    onCategoryClick = { category ->
                        navigateToSearchResult(category)
                    }
                )
            }
        }
    }

    private fun navigateToSearch() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, FragmentSearch())
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToSearchResult(category: String) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, FragmentSearchResult.newInstance(category))
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
