package com.example.commit.ui.point

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.fragment.FragmentMypage

class FragmentPointHistory : Fragment() {

    companion object {
        fun newInstance(hideBottomBar: Boolean): FragmentPointHistory {
            return FragmentPointHistory().apply {
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
                val dummyList = listOf(
                    PointHistoryItem(
                        transactionId = 1,
                        status = "충전",
                        amount = 5000,
                        created_at = "2025-07-04T01:43:00Z"
                    ),
                    PointHistoryItem(
                        transactionId = 2,
                        status = "충전",
                        amount = 50000,
                        created_at = "2025-07-01T12:00:00Z"
                    )
                )

                PointHistoryScreen(
                    pointList = dummyList,
                    onBackClick = {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, FragmentMypage())
                            .addToBackStack(null)
                            .commit()
                    }
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
}
