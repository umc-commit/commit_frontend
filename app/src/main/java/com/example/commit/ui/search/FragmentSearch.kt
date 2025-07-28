package com.example.commit.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.fragment.FragmentHome

class FragmentSearch : Fragment() {

    companion object {
        fun newInstance(hideBottomBar: Boolean): FragmentSearch {
            return FragmentSearch().apply {
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
                SearchScreen(
                    onBackClick = { navigateToHome() },
                    onTotalClick = { navigateToCategory() }
                )
            }
        }
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

    private fun navigateToHome() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, FragmentHome())
            .addToBackStack(null)
            .commit()
    }
    private fun navigateToCategory() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, FragmentCategory())
            .addToBackStack(null)
            .commit()
    }
}
