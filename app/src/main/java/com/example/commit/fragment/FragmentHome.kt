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
import com.example.commit.R
import com.example.commit.activity.AlarmActivity
import com.example.commit.activity.MainActivity
import com.example.commit.activity.ProfileActivity
import com.example.commit.activity.ProfileEditActivity
import com.example.commit.adapter.AuthorCardAdapter
import com.example.commit.adapter.HomeCardAdapter
import com.example.commit.adapter.ReviewCardAdapter
import com.example.commit.databinding.BottomSheetHomeBinding
import com.example.commit.databinding.FragmentHomeBinding
import com.example.commit.ui.post.components.PostHeaderSection
import com.google.android.material.bottomsheet.BottomSheetDialog

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

        binding.ivProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }


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
                        title = "그림 커미션",
                        tags = listOf("그림", "#LD", "#당일마감"),
                        minPrice = 10000,
                        summary = "작업 설명입니다"
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity로부터 전달받은 인자 확인
        val showSignupBottomSheet = arguments?.getBoolean("show_signup_bottom_sheet", false) ?: false

        if (showSignupBottomSheet) {
            showSignupCompleteBottomSheet()
        }
    }

    private fun showSignupCompleteBottomSheet() {
        // BottomSheetDialog 생성 시 TransparentBottomSheetDialog 스타일을 적용합니다.
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        val sheetBinding = BottomSheetHomeBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        // 바텀 시트가 닫힐 때 하단바를 다시 보이도록 리스너 설정
        bottomSheetDialog.setOnDismissListener {
            (activity as? MainActivity)?.showBottomNav(true)
        }

        // 바텀 시트 윈도우 설정 (투명도 60% 효과)
        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0.6f) // 투명도 60% (0.0f는 완전 투명, 1.0f는 완전 불투명)
        }

        bottomSheetDialog.show()

        // 바텀 시트가 나타날 때 하단바 숨기기
        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
