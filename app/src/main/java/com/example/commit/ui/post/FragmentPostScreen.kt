package com.example.commit.ui.post

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commit.activity.MainActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.viewmodel.PostViewModel

class FragmentPostScreen : Fragment() {

    companion object {
        private const val ARG_COMMISSION_ID = "commission_id"

        fun newInstance(commissionId: Int): FragmentPostScreen {
            return FragmentPostScreen().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COMMISSION_ID, commissionId)
                    putBoolean("hideBottomBar", true)
                }
            }
        }
    }

    private val viewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val commissionId = arguments?.getInt(ARG_COMMISSION_ID) ?: -1

        Log.d("FragmentPostScreen", "넘겨받은 commissionId: $commissionId")

        viewModel.loadCommissionDetail(requireContext(), commissionId)

        return ComposeView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent {
                val commission by viewModel.commissionDetail.collectAsState()

                LaunchedEffect(commission) {
                    Log.d("FragmentPostScreen", "commission 데이터 변경됨: $commission")
                }
                commission?.let {
                    PostScreen(
                        title = it.title,
                        tags = listOf(it.category) + it.tags,
                        minPrice = it.minPrice,
                        summary = it.summary,
                        content = it.content,
                        images = it.images.map { image -> image.imageUrl },
                        isBookmarked = it.isBookmarked,
                        imageCount = it.images.size,
                        currentIndex = 0,
                        onReviewListClick = {
                            val intent = Intent(context, WrittenReviewsActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
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