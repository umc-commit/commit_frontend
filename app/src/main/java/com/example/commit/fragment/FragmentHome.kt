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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.doOnAttach
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.savedstate.SavedStateRegistryOwner
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
import com.example.commit.fragment.FragmentChatDetail
import com.example.commit.ui.post.PostScreen
import com.example.commit.ui.search.FragmentSearch
import com.example.commit.viewmodel.PostViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.platform.LocalContext

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

        setBannerTransactionCount(411)
        fetchHomeData()

        binding.rvFollowingPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFollowingPosts.setHasFixedSize(true)

        val commissionId = arguments?.getInt("commission_id") ?: -1
        val showPostDetail = arguments?.getBoolean("show_post_detail", false) ?: false
        if (showPostDetail && commissionId != -1) {
            showPostScreen(commissionId)
            // BottomNavigation 숨기기
            (activity as? MainActivity)?.showBottomNav(false)
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
                    val returnToBookmark = arguments?.getBoolean("return_to_bookmark", false) == true

                    if (binding.frameComposeContainer.visibility == View.VISIBLE) {
                        if (returnToBookmark) {
                            // 상세에서 바로 북마크로 복귀
                            (activity as? MainActivity)?.showBottomNav(true)
                            parentFragmentManager.popBackStack()
                        } else {
                            // 기존 동작: 상세 닫고 홈 노출
                            binding.frameComposeContainer.removeAllViews()
                            binding.frameComposeContainer.visibility = View.GONE
                            (activity as? MainActivity)?.showBottomNav(true)
                        }
                        return
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
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

    private fun showPostScreen(commissionId: Int) {
        val cv = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }

        binding.frameComposeContainer.apply {
            visibility = View.VISIBLE
            removeAllViews()
            addView(cv)
        }

        cv.doOnAttach {
            cv.setContent {
                // Compose 내에서 사용할 안전한 Context
                val context = LocalContext.current

                // ViewTree owners를 명시적으로 주입
                CompositionLocalProvider(
                    LocalLifecycleOwner provides viewLifecycleOwner,
                    LocalSavedStateRegistryOwner provides (viewLifecycleOwner as SavedStateRegistryOwner),
                    LocalViewModelStoreOwner provides this@FragmentHome
                ) {
                    val commission by postViewModel.commissionDetail.collectAsState()

                    LaunchedEffect(commissionId) {
                        postViewModel.loadCommissionDetail(requireContext(), commissionId)
                    }

                    commission?.let { data ->
                        PostScreen(
                            title = data.title,
                            tags = listOf(data.category) + data.tags,
                            minPrice = data.minPrice,
                            summary = data.summary,
                            content = data.content,
                            images = data.images.map { it.imageUrl },
                            isBookmarked = data.isBookmarked,
                            imageCount = data.images.size,
                            currentIndex = 0,
                            commissionId = data.id,
                            onReviewListClick = {
                                startActivity(Intent(requireContext(), WrittenReviewsActivity::class.java))
                            },
                            onChatClick = {
                                // commissionDetail 응답의 artistId는 String이므로 안전 변환
                                val artistIdInt = data.artistId
                                if (artistIdInt == null) {
                                    Log.e("FragmentHome", "artistId가 null 입니다.")
                                    return@PostScreen
                                }

                                // 채팅방 생성 API 호출
                                createChatroomFromHome(
                                    commissionId = data.id,
                                    commissionTitle = data.title,
                                    artistId = artistIdInt,
                                    thumbnailUrl = data.images.firstOrNull()?.imageUrl ?: ""
                                )
                            },
                            onBookmarkToggle = { newState ->
                                // Context는 Compose의 LocalContext 사용
                                postViewModel.toggleBookmark(context, data.id, newState)
                            }
                        )
                    }
                }
            }
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
            adapter = HomeCardAdapter(data.section1) { item -> showPostScreen(item.id) }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvNewRegistrations.apply {
            adapter = HomeCardAdapter(data.section2) { item -> showPostScreen(item.id) }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvHotContent.apply {
            adapter = HomeCardAdapter(data.section3) { item -> showPostScreen(item.id) }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvDeadlineContent.apply {
            adapter = HomeCardAdapter(data.section4) { item -> showPostScreen(item.id) }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.rvReviewContent.apply {
            adapter = ReviewCardAdapter(data.newReview) { /* TODO */ }
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

    // 채팅방 생성 메서드
    // FragmentHome.kt
    private fun createChatroomFromHome(
        commissionId: Int,
        commissionTitle: String,
        artistId: Int,
        thumbnailUrl: String = ""
    ) {
        Log.d("FragmentHome", "createChatroomFromHome 호출 - commissionId: $commissionId, artistId: $artistId, title: $commissionTitle")
        val api = RetrofitObject.getRetrofitService(requireContext())

        val request = RetrofitClient.CreateChatroomRequest(
            artistId = artistId,
            commissionId = commissionId.toString()
        )

        api.createChatroom(request).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                if (!response.isSuccessful) {
                    Log.e("FragmentHome", "채팅방 생성 실패 - code=${response.code()}")
                    return
                }

                val data = response.body()?.success
                if (data == null) {
                    Log.e("FragmentHome", "채팅방 생성 실패 - 응답 없음")
                    return
                }

                val chatroomIdInt = data.id.toIntOrNull()
                
                // ✅ 생성된 채팅방 ID를 삭제된 ID 목록에서 제거
                try {
                    val main = requireActivity() as MainActivity
                    main.chatViewModel.unhideChatroom(requireContext(), chatroomIdInt ?: -1)
                    Log.d("FragmentHome", "채팅방 숨김 해제: $chatroomIdInt")
                } catch (e: Exception) {
                    Log.e("FragmentHome", "unhideChatroom 호출 실패", e)
                }

                // ① 작가 닉네임 조회
                api.getCommissionArtist(commissionId.toString()).enqueue(object :
                    Callback<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>> {
                    override fun onResponse(
                        call: Call<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>>,
                        resp: Response<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>>
                    ) {
                        val nickname = resp.body()?.success?.artist?.nickname ?: ""

                        // ② 닉네임을 authorName으로 넘겨서 채팅 화면 진입
                        val fragment = FragmentChatDetail.newInstanceFromPost(
                            chatName = commissionTitle,
                            authorName = nickname,
                            chatroomId = chatroomIdInt ?: -1,
                            commissionId = commissionId,
                            hasSubmittedApplication = false,
                            sourceFragment = "FragmentHome",
                            thumbnailUrl = thumbnailUrl,
                            artistId = artistId  // ✅ artistId 추가
                        )
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, fragment)
                            .addToBackStack(null)
                            .commit()

                        Toast.makeText(requireContext(), "채팅방이 생성되었습니다", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(
                        call: Call<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>>,
                        t: Throwable
                    ) {
                        Log.e("FragmentHome", "작가 조회 실패: ${t.message}")

                        // 실패해도 화면은 진입하되, authorName은 빈 값(또는 기본값)
                        val fragment = FragmentChatDetail.newInstanceFromPost(
                            chatName = commissionTitle,
                            authorName = "",
                            chatroomId = chatroomIdInt ?: -1,
                            commissionId = commissionId,
                            hasSubmittedApplication = false,
                            sourceFragment = "FragmentHome",
                            thumbnailUrl = thumbnailUrl,
                            artistId = artistId  // ✅ artistId 추가
                        )
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.Nav_Frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                })
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.e("FragmentHome", "채팅방 생성 네트워크 오류", t)
            }
        })
    }
}
