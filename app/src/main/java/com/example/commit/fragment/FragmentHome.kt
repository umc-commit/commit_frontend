package com.example.commit.fragment

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.R
import com.example.commit.activity.alarm.AlarmActivity
import com.example.commit.activity.MainActivity
import com.example.commit.activity.mypage.ProfileActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.adapter.home.AuthorCardAdapter
import com.example.commit.adapter.home.FollowingPostAdapter
import com.example.commit.adapter.home.HomeCardAdapter
import com.example.commit.adapter.home.ReviewCardAdapter
import com.example.commit.databinding.BottomSheetHomeBinding
import com.example.commit.databinding.FragmentHomeBinding
import com.example.commit.ui.search.FragmentSearch
import com.example.commit.ui.post.PostScreen
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.compose.ui.platform.LocalContext
import com.example.commit.ui.post.FragmentPostScreen



class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //전체 거래수 api 연동 시 꼭 지우기
        setBannerTransactionCount(4257)

        // arguments 확인하여 PostHeaderSection 표시
        val showPostDetail = arguments?.getBoolean("show_post_detail", false) ?: false
        val postTitle = arguments?.getString("post_title", "그림 커미션") ?: "그림 커미션"
        
        if (showPostDetail) {
            // PostHeaderSection 표시
            val composeView = ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    val context = LocalContext.current
                    PostScreen(
                        title = postTitle,
                        tags = listOf("그림", "#LD", "#당일마감"),
                        minPrice = 10000,
                        summary = "작업 설명입니다",
                        content = "본문 내용",
                        images = listOf("https://example.com/image1.jpg"),
                        isBookmarked = false,
                        onReviewListClick = {
                            val intent = Intent(context, WrittenReviewsActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }


            binding.frameComposeContainer.apply {
                visibility = View.VISIBLE
                removeAllViews()
                addView(composeView)
            }

            (activity as? MainActivity)?.showBottomNav(false)
        }

        binding.ivProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        // 알람 아이콘 클릭
        binding.ivAlarm.setOnClickListener {
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }

        // 검색 아이콘 클릭 시 FragmentCategory로 이동
        binding.ivSearch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.Nav_Frame, FragmentSearch())
                .addToBackStack(null)
                .commit()
        }

        // 카드 클릭 시 PostHeaderSection 띄우기 + 바텀바 숨기기
        val homeCardClick: (Int) -> Unit = { commissionId ->
            Log.d("FragmentHome", "카드 클릭됨: $commissionId")
            parentFragmentManager.beginTransaction()
                .replace(R.id.Nav_Frame, FragmentPostScreen.newInstance(commissionId))
                .addToBackStack(null)
                .commit()
        }

        val homeReviewClick: (String) -> Unit = { title ->
            Log.d("FragmentHome", "=== homeReviewClick 호출됨 ===")
            Log.d("FragmentHome", "title: $title")

            val composeView = ComposeView(requireContext()).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    val context = LocalContext.current
                    PostScreen(
                        title = "그림 커미션",
                        tags = listOf("그림", "#LD", "#당일마감"),
                        minPrice = 10000,
                        summary = "작업 설명입니다",
                        content = "본문 내용",
                        images = listOf("https://example.com/image1.jpg"),
                        isBookmarked = false,
                        onReviewListClick = {
                            val intent = Intent(context, WrittenReviewsActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }


            binding.frameComposeContainer.apply {
                visibility = View.VISIBLE
                removeAllViews()
                addView(composeView)
            }

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
            adapter = HomeCardAdapter(listOf(1, 1, 1, 1), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewRegistrations.apply {
            adapter = HomeCardAdapter(listOf(1, 1, 1, 1), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvHotContent.apply {
            adapter = HomeCardAdapter(listOf(1, 1, 1, 1), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvDeadlineContent.apply {
            adapter = HomeCardAdapter(listOf(1, 1, 1, 1), homeCardClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvReviewContent.apply {
            adapter = ReviewCardAdapter(listOf("리뷰1", "리뷰2", "리뷰3", "리뷰4"), homeReviewClick)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewAuthorContent.apply {
            adapter = AuthorCardAdapter(listOf("작가1", "작가2", "작가3", "작가4"))
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.tvFollowing.setOnClickListener {
            // 팔로우 탭 UI 표시
            binding.rvFollowingPosts.visibility = View.VISIBLE
            binding.nestedScrollView.visibility = View.GONE

            // 탭 텍스트 색상 변경
            binding.tvRecommend.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            binding.tvFollowing.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))

            // indicator 이동
            binding.indicatorRecommend.visibility = View.GONE
            binding.indicatorFollowing?.visibility = View.VISIBLE

            // 리스트 어댑터 설정
            val dummyList = listOf("타임글1", "타임글2", "타임글3", "타입글4", "타입글5")
            binding.rvFollowingPosts.apply {
                adapter = FollowingPostAdapter(dummyList) {
                    // iv_more 클릭 시 바텀시트 띄우는 로직
                    val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_post_more, null)
                    val bottomSheetDialog = BottomSheetDialog(requireContext())
                    bottomSheetDialog.setContentView(bottomSheetView)

                    bottomSheetDialog.window?.apply {
                        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        setDimAmount(0.6f)
                    }

                    bottomSheetDialog.show()
                }
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        binding.tvRecommend.setOnClickListener {
            // 추천 콘텐츠 표시
            binding.nestedScrollView.visibility = View.VISIBLE
            binding.rvFollowingPosts.visibility = View.GONE

            // 탭 텍스트 색상 변경
            binding.tvRecommend.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.tvFollowing.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))

            // indicator 변경
            binding.indicatorRecommend.visibility = View.VISIBLE
            binding.indicatorFollowing?.visibility = View.GONE
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

    private fun setBannerTransactionCount(count: Int) {
        val number = String.format("%,d", count)
        val color = ContextCompat.getColor(requireContext(), R.color.mint1)

        val builder = SpannableStringBuilder()
            .append("현재 ")
            .append(SpannableString(number).apply {
                setSpan(ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            })
            .append("건의 거래가 완료됐어요.")

        binding.root.findViewById<TextView>(R.id.tv_banner_text).text = builder
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
