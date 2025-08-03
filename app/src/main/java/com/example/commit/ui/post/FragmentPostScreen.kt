package com.example.commit.ui.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.activity.MainActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.ui.post.components.PostHeaderSection
import com.example.commit.ui.request.components.Commission
import java.io.Serializable

class FragmentPostScreen : Fragment() {

    companion object {
        private const val ARG_COMMISSION = "commission"

        fun newInstance(commission: Commission): FragmentPostScreen {
            return FragmentPostScreen().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_COMMISSION, commission)
                    putBoolean("hideBottomBar", true)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val commission = arguments?.getSerializable(ARG_COMMISSION) as? Commission

        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                PostHeaderSection(
                    title = commission?.title ?: "제목 없음",
                    tags = commission?.tags ?: emptyList(),
                    minPrice = 30000,
                    summary = "이 커미션은 작가가 직접 운영하는 커미션입니다.",
                    onReviewListClick = {
                        val intent = Intent(requireContext(), WrittenReviewsActivity::class.java)
                        startActivity(intent)
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
