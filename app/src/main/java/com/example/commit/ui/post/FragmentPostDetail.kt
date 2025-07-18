package com.example.commit.ui.post

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.commit.activity.MainActivity



class FragmentPostDetail : Fragment() {

    companion object {
        fun newInstance(hideBottomBar: Boolean): FragmentPostDetail {
            return FragmentPostDetail().apply {
                arguments = Bundle().apply {
                    putBoolean("hideBottomBar", hideBottomBar)
                }
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
}
