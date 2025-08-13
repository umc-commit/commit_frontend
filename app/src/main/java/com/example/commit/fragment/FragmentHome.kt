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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.activity.WrittenReviewsActivity
import com.example.commit.activity.alarm.AlarmActivity
import com.example.commit.activity.mypage.ProfileActivity
import com.example.commit.adapter.home.AuthorCardAdapter
import com.example.commit.adapter.home.FollowingPostAdapter
import com.example.commit.adapter.home.HomeCardAdapter
import com.example.commit.adapter.home.ReviewCardAdapter
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.BottomSheetHomeBinding
import com.example.commit.databinding.FragmentHomeBinding
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.ui.post.PostScreen
import com.example.commit.ui.search.FragmentSearch
import com.example.commit.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        binding.ivAlarm.setOnClickListener {
            startActivity(Intent(requireContext(), AlarmActivity::class.java))
        }

        binding.ivSearch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.Nav_Frame, FragmentSearch())
                .addToBackStack(null)
                .commit()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.frameComposeContainer.visibility == View.VISIBLE) {
                        binding.frameComposeContainer.visibility = View.GONE
                        (activity as? MainActivity)?.showBottomNav(true)
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )

        binding.tvFollowing.setOnClickListener {
            binding.rvFollowingPosts.visibility = View.VISIBLE
            binding.nestedScrollView.visibility = View.GONE
            binding.tvRecommend.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray2))
            binding.tvFollowing.setTextColor(ContextCompat.getColor(requireContext(), R.color.black1))
            binding.indicatorRecommend.visibility = View.GONE
            binding.indicatorFollowing?.visibility = View.VISIBLE

            if (followingLoaded) {
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
                            },
                            onItemClick = { clickedCommissionId ->
                                // (어댑터 콜백이 commissionId만 넘기는 형태라면 작가정보는 null)
                                showPostScreen(clickedCommissionId.toInt())
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

    // 홈 데이터 호출
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
                        Log.e("HomeAPI", "success null\ndata 없음")
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
                showPostScreen(item.id, item.artist.id, item.artist.nickname)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvNewRegistrations.apply {
            adapter = HomeCardAdapter(data.section2) { item ->
                showPostScreen(item.id, item.artist.id, item.artist.nickname)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvHotContent.apply {
            adapter = HomeCardAdapter(data.section3) { item ->
                showPostScreen(item.id, item.artist.id, item.artist.nickname)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvDeadlineContent.apply {
            adapter = HomeCardAdapter(data.section4) { item ->
                showPostScreen(item.id, item.artist.id, item.artist.nickname)
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

    binding.rvReviewContent.apply {
            adapter = ReviewCardAdapter(data.newReview) { /* 필요시 클릭 처리 */ }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.rvNewAuthorContent.apply {
            adapter = AuthorCardAdapter(data.newArtist)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun showPostScreen(
        commissionId: Int,
        artistId: Int? = null,
        artistName: String? = null
    ) {
        // 홈에서 받은 작가 정보 캐시
        postViewModel.setArtistFromHome(artistId, artistName)

        val composeView = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val commission by postViewModel.commissionDetail.collectAsState()
                val cachedArtistId by postViewModel.artistId.collectAsState()
                val cachedArtistName by postViewModel.artistName.collectAsState()

                LaunchedEffect(commissionId) {
                    postViewModel.loadCommissionDetail(requireContext(), commissionId)
                }

                commission?.let { detail ->
                    PostScreen(
                        title = detail.title,
                        tags = listOf(detail.category) + detail.tags,
                        minPrice = detail.minPrice,
                        summary = detail.summary,
                        content = detail.content,
                        images = detail.images.map { it.imageUrl },
                        isBookmarked = detail.isBookmarked,
                        imageCount = detail.images.size,
                        currentIndex = 0,
                        commissionId = detail.id,
                        onReviewListClick = {
                            startActivity(Intent(requireContext(), WrittenReviewsActivity::class.java))
                        },
                        onChatClick = {
                            val aId = cachedArtistId
                            val aName = cachedArtistName
                            if (aId == null) {
                                Toast.makeText(requireContext(), "작가 정보를 불러오는 중입니다.", Toast.LENGTH_SHORT).show()
                                return@PostScreen
                            }
                            fetchRequestIdAndCreateChatroom(
                                commissionId = detail.id,
                                commissionTitle = detail.title,
                                artistId = aId,
                                artistName = aName.orEmpty()
                            )
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


    // 신청목록에서 requestId 찾아 채팅방 생성
    private fun fetchRequestIdAndCreateChatroom(
        commissionId: Int,
        commissionTitle: String,
        artistId: Int,
        artistName: String
    ) {
        lifecycleScope.launch {
            try {
                val api = RetrofitObject.getRetrofitService(requireContext())
                val reqList = api.getRequestList() // suspend

                // 1) requests 안전하게 가져오기
                val requests = reqList.success?.requests ?: emptyList()

                // 2) commissionId 매칭
                val matched = requests.find { it.commission.id == commissionId }

                // 3) requestId 추출
                val requestId = matched?.requestId

                if (requestId == null) {
                    Toast.makeText(requireContext(), "이 커미션에 대한 신청이 없습니다.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // 4) 채팅방 생성
                createChatroom(
                    commissionId = commissionId,
                    commissionTitle = commissionTitle,
                    artistId = artistId,
                    artistName = artistName,
                    requestId = requestId
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "요청 목록 조회 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createChatroom(
        commissionId: Int,
        commissionTitle: String,
        artistId: Int,
        artistName: String,
        requestId: Int
    ) {
        val api = RetrofitObject.getRetrofitService(requireContext())
        val currentUserId = 1 // TODO: 로그인 유저 ID(토큰)로 교체

        val body = RetrofitClient.CreateChatroomRequest(
            consumerId = currentUserId,
            artistId = artistId,
            requestId = requestId
        )

        api.createChatroom(body).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                val data = response.body()?.success
                if (response.isSuccessful && data != null) {
                    openChatScreen(data.id, commissionTitle, artistName, commissionId)
                } else {
                    Toast.makeText(requireContext(), "채팅방 생성 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun openChatScreen(
        chatroomId: Int,
        commissionTitle: String,
        artistName: String,
        commissionId: Int
    ) {
        val fragment = FragmentPostChatDetail().apply {
            arguments = bundleOf(
                "chatName" to commissionTitle,
                "authorName" to artistName,
                "chatroomId" to chatroomId,
                "sourceFragment" to "FragmentHome",
                "commissionId" to commissionId
            )
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, fragment)
            .addToBackStack(null)
            .commit()
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

    private fun showSignupCompleteBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.TransparentBottomSheetDialog)
        val sheetBinding = BottomSheetHomeBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)
        bottomSheetDialog.setOnDismissListener { (activity as? MainActivity)?.showBottomNav(true) }
        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.6f)
        }
        bottomSheetDialog.show()
        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val showSignupBottomSheet = arguments?.getBoolean("show_signup_bottom_sheet", false) ?: false
        if (showSignupBottomSheet) showSignupCompleteBottomSheet()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
