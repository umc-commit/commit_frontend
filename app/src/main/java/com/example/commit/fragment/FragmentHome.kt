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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.BottomSheetHomeBinding
import com.example.commit.databinding.FragmentHomeBinding
import com.example.commit.ui.search.FragmentSearch
import com.example.commit.ui.post.PostScreen
import com.example.commit.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.fragment.app.viewModels
import androidx.core.os.bundleOf
import com.example.commit.fragment.FragmentPostChatDetail


class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val postViewModel: PostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setBannerTransactionCount(4257)
        fetchHomeData()

        val commissionId = arguments?.getInt("commission_id") ?: -1
        val showPostDetail = arguments?.getBoolean("show_post_detail", false) ?: false
        if (showPostDetail && commissionId != -1) {
            showPostScreen(commissionId)
        }

        binding.ivProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.ivAlarm.setOnClickListener {
            val intent = Intent(requireContext(), AlarmActivity::class.java)
            startActivity(intent)
        }

        binding.ivSearch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.Nav_Frame, FragmentSearch())
                .addToBackStack(null)
                .commit()
        }

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

        binding.tvFollowing.setOnClickListener {
            binding.rvFollowingPosts.visibility = View.VISIBLE
            binding.nestedScrollView.visibility = View.GONE
            binding.tvRecommend.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            binding.tvFollowing.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.indicatorRecommend.visibility = View.GONE
            binding.indicatorFollowing?.visibility = View.VISIBLE

            val dummyList = listOf("타임글1", "타임글2", "타임글3", "타입글4", "타입글5")
            binding.rvFollowingPosts.apply {
                adapter = FollowingPostAdapter(dummyList) {
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
            binding.nestedScrollView.visibility = View.VISIBLE
            binding.rvFollowingPosts.visibility = View.GONE
            binding.tvRecommend.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.tvFollowing.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            binding.indicatorRecommend.visibility = View.VISIBLE
            binding.indicatorFollowing?.visibility = View.GONE
        }

        return binding.root
    }

    private fun showPostScreen(commissionId: Int) {
        val composeView = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val commission by postViewModel.commissionDetail.collectAsState()

                LaunchedEffect(commissionId) {
                    postViewModel.loadCommissionDetail(requireContext(), commissionId)
                }

                commission?.let {
                    PostScreen(
                        title = it.title,
                        tags = listOf(it.category) + it.tags,
                        minPrice = it.minPrice,
                        summary = it.summary,
                        content = it.content,
                        images = it.images.map { img -> img.imageUrl },
                        isBookmarked = it.isBookmarked,
                        imageCount = it.images.size,
                        currentIndex = 0,
                        commissionId = it.id,
                        onReviewListClick = {
                            val intent = Intent(requireContext(), WrittenReviewsActivity::class.java)
                            startActivity(intent)
                        },
                        onChatClick = {
                            Log.d("FragmentHome", "채팅하기 버튼 클릭 - 커미션 ID: ${it.id}, 제목: ${it.title}")
                            // FragmentHome에서도 채팅방 생성 기능 구현
                            createChatroomFromHome(it.id, it.title)
                        }
                    )
                }
            }
        }

        binding.frameComposeContainer.apply {
            visibility = View.VISIBLE
            removeAllViews()
            addView(composeView)
        }

        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showSignupBottomSheet = arguments?.getBoolean("show_signup_bottom_sheet", false) ?: false
        if (showSignupBottomSheet) {
            showSignupCompleteBottomSheet()
        }
    }

    private fun showSignupCompleteBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        val sheetBinding = BottomSheetHomeBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        bottomSheetDialog.setOnDismissListener {
            (activity as? MainActivity)?.showBottomNav(true)
        }

        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.6f)
        }

        bottomSheetDialog.show()
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

    private fun fetchHomeData() {
        val api = RetrofitObject.getRetrofitService(requireContext())

        api.getHomeData().enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>> {

            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        setupHomeAdapters(data)
                        Log.d("HomeAPI", "성공")
                    } else {
                        Log.e("HomeAPI", "success null\\ndata 없음")
                    }
                } else {
                    Log.e("HomeAPI", "API 실패: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>>,
                t: Throwable
            ) {
                Log.e("HomeAPI", "네트워크 오류", t)
            }
        })
    }

    private fun setupHomeAdapters(data: RetrofitClient.HomeResponseData) {
        binding.rvTodayRecommendations.apply {
            adapter = HomeCardAdapter(data.section1) { item ->
                showPostScreen(item.id)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewRegistrations.apply {
            adapter = HomeCardAdapter(data.section2) { item ->
                showPostScreen(item.id)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvHotContent.apply {
            adapter = HomeCardAdapter(data.section3) { item ->
                showPostScreen(item.id)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvDeadlineContent.apply {
            adapter = HomeCardAdapter(data.section4) { item ->
                showPostScreen(item.id)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvReviewContent.apply {
            adapter = ReviewCardAdapter(data.newReview) { /* TODO: 리뷰 클릭 이벤트 정의 */ }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewAuthorContent.apply {
            adapter = AuthorCardAdapter(data.newArtist)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    private fun createChatroomFromHome(commissionId: Int, commissionTitle: String) {
        Log.d("FragmentHome", "createChatroomFromHome 메서드 호출됨 - commissionId: $commissionId, title: $commissionTitle")
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        // 임시 값들 (실제로는 SharedPreferences나 다른 방법으로 가져와야 함)
        val currentUserId = 1
        // commissionId에 따라 다른 artistId 사용하여 새 채팅방 생성 시도
        val artistId = if (commissionId % 2 == 0) 2 else 1 // 커미션 ID에 따라 다른 작가
        val artistName = if (artistId == 1) "키르" else "작가2" // 작가에 따른 이름
        
        // 임시: 실제로는 커미션 신청 후 생성되는 requestId를 사용해야 함
        // 일단 기존에 존재하는 requestId 사용 (3번이 존재함을 로그에서 확인)
        val tempRequestId = 3 // 기존 존재하는 request ID 사용
        val request = RetrofitClient.CreateChatroomRequest(
            consumerId = currentUserId,
            artistId = artistId,
            requestId = tempRequestId
        )
        Log.d("FragmentHome", "임시 requestId 사용: $tempRequestId (원래 커미션ID: $commissionId)")

        Log.d("FragmentHome", "API 호출 시작 - request: $request")
        api.createChatroom(request).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                Log.d("FragmentHome", "API 응답 받음: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                Log.d("FragmentHome", "응답 바디: ${response.body()}")
                
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("FragmentHome", "채팅방 생성 성공: ${data.id}")
                        
                        // 생성된 채팅방으로 이동
                        val fragment = FragmentPostChatDetail().apply {
                            arguments = bundleOf(
                                "chatName" to commissionTitle,
                                "authorName" to artistName,
                                "chatroomId" to data.id,
                                "sourceFragment" to "FragmentHome",
                                "commissionId" to commissionId
                            )
                        }
                        
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, fragment)
                            .addToBackStack(null)
                            .commit()
                        
                        Toast.makeText(
                            requireContext(),
                            "채팅방이 생성되었습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.e("FragmentHome", "채팅방 생성 실패: success 데이터가 없음")
                        Toast.makeText(
                            requireContext(),
                            "채팅방 생성에 실패했습니다",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("FragmentHome", "채팅방 생성 실패: ${response.code()}")
                    Log.e("FragmentHome", "에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(
                        requireContext(),
                        "채팅방 생성에 실패했습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.e("FragmentHome", "채팅방 생성 네트워크 오류: ${t.message}", t)
                Toast.makeText(
                    requireContext(),
                    "네트워크 오류가 발생했습니다: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


}
