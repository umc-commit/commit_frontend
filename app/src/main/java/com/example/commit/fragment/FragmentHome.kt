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
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.dto.CommissionDetailResponse
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
    private var followingLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setBannerTransactionCount(4257)
        fetchHomeData()

        binding.rvFollowingPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFollowingPosts.setHasFixedSize(true)

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

            if (followingLoaded) {
                // 탭 전환만
                binding.rvFollowingPosts.visibility = View.VISIBLE
                binding.nestedScrollView.visibility = View.GONE
                return@setOnClickListener
            }

            val api = RetrofitObject.getRetrofitService(requireContext())
            api.getFollowing(page = 1, limit = 10)
                .enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.FollowingResponseData>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowingResponseData>>,
                        response: Response<RetrofitClient.ApiResponse<RetrofitClient.FollowingResponseData>>
                    ) {
                        followingLoaded = true
                        val items = response.body()?.success?.items ?: emptyList()

                        // 어댑터 생성/세팅
                        binding.rvFollowingPosts.adapter = FollowingPostAdapter(
                            postList = items,
                            onMoreClick = {
                                val view = layoutInflater.inflate(R.layout.bottom_sheet_post_more, null)
                                val dialog = BottomSheetDialog(requireContext())
                                dialog.setContentView(view)
                                dialog.window?.apply {
                                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    setDimAmount(0.6f)
                                }
                                dialog.show()
                            }
                        )
                    }
                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowingResponseData>>,
                        t: Throwable
                    ) {
                        Log.d("FragmentHome", "팔로잉 목록 실패: ${t.message}")
                    }
                })
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
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        // 먼저 커미션 상세 정보를 조회해서 artistId를 가져옴
        api.getCommissionDetail(commissionId).enqueue(object : Callback<CommissionDetailResponse> {
            override fun onResponse(
                call: Call<CommissionDetailResponse>,
                response: Response<CommissionDetailResponse>
            ) {
                Log.d("FragmentHome", "커미션 상세 조회 응답: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                
                if (response.isSuccessful) {
                    val commissionData = response.body()?.success
                    Log.d("FragmentHome", "커미션 응답 전체: ${response.body()}")
                    Log.d("FragmentHome", "커미션 success 데이터: $commissionData")
                    
                    if (commissionData != null) {
                        val artistId = commissionData.artistId
                        Log.d("FragmentHome", "커미션에서 가져온 artistId: $artistId")
                        Log.d("FragmentHome", "커미션 전체 데이터: $commissionData")
                        
                        // artistId가 0이면 다른 방법으로 시도
                        if (artistId == 0) {
                            Log.w("FragmentHome", "artistId가 0입니다. 다른 방법으로 시도합니다.")
                            // 임시로 commissionId를 artistId로 사용 (테스트용)
                            val tempArtistId = if (commissionId % 2 == 0) 2 else 1
                            Log.d("FragmentHome", "임시 artistId 사용: $tempArtistId")
                            getUserProfileAndCreateChatroom(api, tempArtistId, commissionId, commissionTitle)
                        } else {
                            // 정상적인 artistId 사용
                            getUserProfileAndCreateChatroom(api, artistId, commissionId, commissionTitle)
                        }
                    } else {
                        Log.e("FragmentHome", "커미션 데이터가 없음")
                        Toast.makeText(requireContext(), "커미션 정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FragmentHome", "커미션 상세 조회 실패: ${response.code()}")
                    Log.e("FragmentHome", "커미션 에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "커미션 정보 조회에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<CommissionDetailResponse>,
                t: Throwable
            ) {
                Log.e("FragmentHome", "커미션 상세 조회 네트워크 오류", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserProfileAndCreateChatroom(
        api: RetrofitAPI,
        artistId: Int,
        commissionId: Int,
        commissionTitle: String
    ) {
        // 사용자 프로필을 조회해서 실제 userId 사용
        api.getMyProfile().enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>
            ) {
                Log.d("FragmentHome", "프로필 조회 응답: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                Log.d("FragmentHome", "프로필 응답 바디: ${response.body()}")
                Log.d("FragmentHome", "프로필 에러 응답: ${response.errorBody()?.string()}")
                
                if (response.isSuccessful) {
                    val profileData = response.body()?.success
                    if (profileData != null) {
                        val currentUserId = profileData.user.userId?.toIntOrNull() ?: 1
                        Log.d("FragmentHome", "현재 사용자 ID: $currentUserId")
                        Log.d("FragmentHome", "사용자 정보: ${profileData.user}")
                        
                        // 실제 사용자 ID와 아티스트 ID로 채팅방 생성
                        createChatroomWithCorrectData(api, currentUserId, artistId, commissionId, commissionTitle)
                    } else {
                        Log.e("FragmentHome", "프로필 데이터가 없음")
                        Log.e("FragmentHome", "전체 응답: ${response.body()}")
                        Toast.makeText(requireContext(), "사용자 정보를 가져올 수 없습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FragmentHome", "프로필 조회 실패: ${response.code()}")
                    Log.e("FragmentHome", "프로필 에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "사용자 정보 조회에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>,
                t: Throwable
            ) {
                Log.e("FragmentHome", "프로필 조회 네트워크 오류", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createChatroomWithCorrectData(
        api: RetrofitAPI,
        consumerId: Int,
        artistId: Int,
        commissionId: Int,
        commissionTitle: String
    ) {
        // commissionId를 requestId로 사용 (또는 별도로 생성)
        val requestId = commissionId
        
        Log.d("FragmentHome", "올바른 데이터로 채팅방 생성 시도")
        Log.d("FragmentHome", "consumerId: $consumerId, artistId: $artistId, requestId: $requestId")
        
        val request = RetrofitClient.CreateChatroomRequest(
            consumerId = consumerId,
            artistId = artistId,
            requestId = requestId
        )
        Log.d("FragmentHome", "API 호출 시작 - request: $request")
        
        api.createChatroom(request).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                Log.d("FragmentHome", "API 응답 받음: code=${response.code()}, isSuccessful=${response.isSuccessful}")
                Log.d("FragmentHome", "응답 바디: ${response.body()}")
                Log.d("FragmentHome", "에러 응답: ${response.errorBody()?.string()}")
                
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("FragmentHome", "채팅방 생성 성공: ${data.id}")
                        
                        // 생성된 채팅방으로 이동
                        val fragment = FragmentPostChatDetail().apply {
                            arguments = bundleOf(
                                "chatName" to commissionTitle,
                                "authorName" to "작가", // 실제 아티스트 이름을 사용할 수 있음
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
                        Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("FragmentHome", "채팅방 생성 실패: ${response.code()}")
                    Log.e("FragmentHome", "에러 응답: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.e("FragmentHome", "채팅방 생성 네트워크 오류: ${t.message}", t)
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
