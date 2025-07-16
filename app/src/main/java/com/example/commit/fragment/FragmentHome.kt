package com.example.commit.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.activity.AlarmActivity
import com.example.commit.activity.MainActivity
import com.example.commit.adapter.AuthorCardAdapter
import com.example.commit.adapter.HomeCardAdapter
import com.example.commit.adapter.ReviewCardAdapter
import com.example.commit.databinding.FragmentHomeBinding
import com.example.commit.ui.post.components.PostHeaderSection

class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "FragmentHome"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "onCreateView 시작")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.ivAlarm.setOnClickListener {
            Log.d(TAG, "알람 아이콘 클릭")
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }

        // 카드 클릭 시 PostHeaderSection 띄우기 + 바텀바 숨기기
        val homeCardClick: (String) -> Unit = { title ->
            Log.d(TAG, "카드 클릭됨: $title")

            val composeView = ComposeView(requireContext()).apply {
                setContent {
                    Log.d(TAG, "ComposeView.setContent 실행됨")
                    PostHeaderSection(
                        title = title,
                        tags = listOf("태그1", "#예시", "#테스트"),
                        minPrice = 10000,
                        summary = "$title 커미션에 대한 설명입니다."
                    )
                }
            }

            binding.frameComposeContainer.apply {
                visibility = View.VISIBLE
                removeAllViews()
                addView(composeView)
            }

            // 바텀바 숨기기
            (activity as? MainActivity)?.showBottomNav(false)
        }

        // 뒤로가기 누르면 바텀바 다시 보이기
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.frameComposeContainer.visibility == View.VISIBLE) {
                    binding.frameComposeContainer.visibility = View.GONE
                    (activity as? MainActivity)?.showBottomNav(true)
                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })

        // RecyclerView 설정
        binding.rvTodayRecommendations.apply {
            adapter = HomeCardAdapter(listOf("추천1", "추천2", "추천3", "추천4"), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewRegistrations.apply {
            adapter = HomeCardAdapter(listOf("추천5", "추천6", "추천7", "추천8"), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvHotContent.apply {
            adapter = HomeCardAdapter(listOf("추천9", "추천10", "추천11", "추천12"), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvDeadlineContent.apply {
            adapter = HomeCardAdapter(listOf("추천13", "추천14", "추천15", "추천16"), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvReviewContent.apply {
            adapter = ReviewCardAdapter(listOf("리뷰1", "리뷰2", "리뷰3", "리뷰4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewAuthorContent.apply {
            adapter = AuthorCardAdapter(listOf("작가1", "작가2", "작가3", "작가4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
