package com.example.commit.activity.author

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.adapter.author.AuthorCommissionAdapter
import com.example.commit.adapter.author.AuthorReviewAdapter
import com.example.commit.adapter.mypage.BadgeAdapter
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.ActivityAuthorProfileBinding
import com.example.commit.databinding.BottomSheetPostMoreBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.example.commit.ui.post.PostScreen
import com.example.commit.viewmodel.PostViewModel


class AuthorProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorProfileBinding
    private var isFollowing = false
    private var isFollowLoading = false
    private var artistIdFromIntent: Int = -1
    private lateinit var postViewModel: PostViewModel
    private var composeOverlay: FrameLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 기본 RecyclerView 초기화 (빈 어댑터)
        initRecyclerViews()

        // 팔로우 버튼
        binding.btnFollowing.setOnClickListener {
            if (artistIdFromIntent == -1 || isFollowLoading) return@setOnClickListener
            if (isFollowing) {
                callUnfollow(artistIdFromIntent)
            } else {
                callFollow(artistIdFromIntent)
            }
        }

        artistIdFromIntent = intent.getIntExtra("artistId", -1)
        if (artistIdFromIntent != -1) {
            // 서버 데이터 로드
            loadAuthorProfile(artistIdFromIntent)

            // 로컬에 저장해둔 팔로우 상태로 '버튼 UI만' 먼저 맞춤 (카운트는 그대로)
            setFollowVisualState(loadFollowState(artistIdFromIntent))

            // 팔로워 수도 로컬 값 있으면 복원
            loadFollowerCount(artistIdFromIntent)?.let { cached ->
                setFollowerCountText(cached)
            }
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.ivMore.setOnClickListener { showSortBottomSheet() }

        // 후기 섹션 초기 숨김
        var isReviewExpanded = false
        binding.recyclerReviews.visibility = View.GONE
        binding.ivDropdown.setOnClickListener {
            isReviewExpanded = !isReviewExpanded
            binding.ivDropdown.setImageResource(
                if (isReviewExpanded) R.drawable.ic_dropup else R.drawable.ic_dropdown
            )
            toggleReviewSection(isReviewExpanded)
        }

        // 채팅 버튼
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "chat")
            startActivity(intent)
            finish()
        }

        postViewModel = ViewModelProvider(this).get(PostViewModel::class.java)

        // 뒤로가기: 상세 오버레이가 떠 있으면 닫기
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (composeOverlay != null && composeOverlay!!.parent != null) {
                    (composeOverlay!!.parent as ViewGroup).removeView(composeOverlay)
                    composeOverlay = null
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    // 버튼 이미지/상태만 바꾸는 함수 (팔로워 수 변동 없음)
    private fun setFollowVisualState(state: Boolean) {
        isFollowing = state
        binding.btnFollowing.setBackgroundResource(
            if (isFollowing) R.drawable.pf_follow else R.drawable.pf_unfollow
        )
    }

    private fun saveFollowerCount(artistId: Int, count: Int) {
        val prefs = getSharedPreferences("follow_state", MODE_PRIVATE)
        prefs.edit().putInt("artist_${artistId}_count", count).apply()
    }

    private fun loadFollowerCount(artistId: Int): Int? {
        val prefs = getSharedPreferences("follow_state", MODE_PRIVATE)
        return if (prefs.contains("artist_${artistId}_count")) prefs.getInt("artist_${artistId}_count", 0) else null
    }

    private fun getCurrentFollowerCountFromUI(): Int {
        val t = binding.tvFollowerCount.text.toString()
        return t.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
    }

    private fun setFollowerCountText(count: Int) {
        binding.tvFollowerCount.text = "팔로워 $count"
    }

    private fun initRecyclerViews() {
        // 배지 RecyclerView
        binding.recyclerBadges.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 커미션 RecyclerView
        binding.recyclerCard.layoutManager = LinearLayoutManager(this)
        binding.recyclerCard.adapter = AuthorCommissionAdapter(mutableListOf()) { }

        // 리뷰 RecyclerView
        binding.recyclerReviews.layoutManager = LinearLayoutManager(this)
        binding.recyclerReviews.adapter = AuthorReviewAdapter(emptyList())
    }

    private fun showSortBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetBinding = BottomSheetPostMoreBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0.6f)
        }
        bottomSheetDialog.show()
    }

    private fun updateSlots(remainingSlots: Int, totalSlots: Int = 4) {
        val clampedRemaining = remainingSlots.coerceIn(0, totalSlots)
        val slotIcons = listOf(binding.slot1, binding.slot2, binding.slot3, binding.slot4)
        val filledCount = totalSlots - clampedRemaining

        for (i in slotIcons.indices) {
            slotIcons[i].setImageResource(
                if (i < filledCount) R.drawable.ic_slot_filled else R.drawable.ic_slot_empty
            )
        }
        binding.tvRemainingSlots.text = "남은 슬롯 ${clampedRemaining}개"
    }

    private fun toggleReviewSection(show: Boolean) {
        if (show) {
            binding.recyclerReviews.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(200).start()
            }
        } else {
            binding.recyclerReviews.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction { binding.recyclerReviews.visibility = View.GONE }
                .start()
        }
    }

    private fun loadAuthorProfile(artistId: Int) {
        val service = RetrofitObject.getRetrofitService(this)

        service.getAuthorProfile(artistId)
            .enqueue(object : Callback<RetrofitClient.AuthorProfileResponse> {
                override fun onResponse(
                    call: Call<RetrofitClient.AuthorProfileResponse>,
                    response: Response<RetrofitClient.AuthorProfileResponse>
                ) {
                    if (!response.isSuccessful) return
                    val data = response.body()?.success ?: return

                    // 프로필
                    binding.tvUsername.text = data.nickname
                    binding.tvIntroContent.text = data.description
                    Glide.with(this@AuthorProfileActivity)
                        .load(data.profileImage?.takeIf { it.isNotBlank() } ?: R.drawable.ic_profile)
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(binding.ivProfile)
                    updateSlots(data.slot)

                    // 1. badges 변환
                    // 기존 변환부 교체
                    val convertedBadges = data.badges.mapNotNull { ub ->
                        // badge 배열에서 첫 번째 배지 사용 (여러 개면 첫 것, 없으면 skip)
                        val first = ub.badge.firstOrNull() ?: return@mapNotNull null
                        val badgeDetail = RetrofitClient.BadgeDetail(
                            id = first.id,
                            type = first.type,
                            threshold = first.threshold,
                            name = first.name,
                            badgeImage = first.badgeImage
                        )
                        RetrofitClient.UserBadge(
                            id = ub.id,
                            earnedAt = ub.earnedAt,
                            badge = listOf(badgeDetail)
                        )
                    }
                    binding.recyclerBadges.adapter = BadgeAdapter(
                        badgeList = convertedBadges,
                        context = this@AuthorProfileActivity
                    ) { dialog -> dialog.show() }


                    // 커미션
                    binding.recyclerCard.adapter = AuthorCommissionAdapter(
                        data.commissions.toMutableList()
                    ) { commissionId ->
                        showPostScreen(commissionId)   // 상세화면 진입
                    }

                    // 리뷰
                    binding.recyclerReviews.adapter =
                        AuthorReviewAdapter(data.reviews)
                }

                override fun onFailure(
                    call: Call<RetrofitClient.AuthorProfileResponse>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                }
            })
    }

    private fun saveFollowState(artistId: Int, following: Boolean) {
        val prefs = getSharedPreferences("follow_state", MODE_PRIVATE)
        prefs.edit().putBoolean("artist_$artistId", following).apply()
    }

    private fun loadFollowState(artistId: Int): Boolean {
        val prefs = getSharedPreferences("follow_state", MODE_PRIVATE)
        return prefs.getBoolean("artist_$artistId", false)
    }


    private fun callFollow(artistId: Int) {
        val service = RetrofitObject.getRetrofitService(this)
        setFollowLoading(true)

        service.followArtist(artistId).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>
            ) {
                Log.d("AuthorProfileActivity", "follow code=${response.code()} isOk=${response.isSuccessful}")
                Log.d("AuthorProfileActivity", "body=${response.body()}")
                Log.d("AuthorProfileActivity", "error=${response.errorBody()?.string()}")
                setFollowLoading(false)
                val body = response.body()
                val ok = response.isSuccessful && body?.resultType == "SUCCESS" && body.success != null
                if (ok) {
                    applyFollowState(true); return
                }

                // 이미 팔로우 중 → 성공으로 간주하여 동기화
                val already = response.code() == 409 ||
                        body?.error?.errorCode == "ALREADY_FOLLOWED" ||
                        (body?.error?.reason?.contains("이미", true) == true
                                && body.error.reason.contains("팔로우"))
                if (already) {
                    applyFollowState(true)
                } else {
                    showLog(body?.error?.reason ?: "팔로우에 실패했습니다.")
                    Log.d("AuthorProfileActivity", "팔로우 실패(서버): ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>,
                t: Throwable
            ) {
                setFollowLoading(false)
                showLog("네트워크 오류로 팔로우에 실패했습니다.")
            }
        })
    }

    private fun callUnfollow(artistId: Int) {
        val service = RetrofitObject.getRetrofitService(this)
        setFollowLoading(true)

        service.unfollowArtist(artistId).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>
            ) {
                setFollowLoading(false)
                val body = response.body()
                val ok = response.isSuccessful && body?.resultType == "SUCCESS" && body.success != null
                if (ok) {
                    applyFollowState(false); return
                }

                // 이미 언팔 상태 → 성공으로 간주하여 동기화
                val already = response.code() == 409 ||
                        body?.error?.errorCode == "ALREADY_UNFOLLOWED" ||
                        (body?.error?.reason?.contains("이미", true) == true
                                && body.error.reason.contains("취소"))
                if (already) {
                    applyFollowState(false)
                } else {
                    showLog(body?.error?.reason ?: "팔로우 취소에 실패했습니다.")
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>,
                t: Throwable
            ) {
                setFollowLoading(false)
                showLog("네트워크 오류로 팔로우 취소에 실패했습니다.")
            }
        })
    }

    private fun setFollowLoading(loading: Boolean) {
        isFollowLoading = loading
        binding.btnFollowing.isEnabled = !loading
    }

    private fun applyFollowState(newFollowing: Boolean, overrideCount: Int? = null) {
        val currentNumber = getCurrentFollowerCountFromUI()
        val nextNumber = overrideCount ?: when {
            !isFollowing && newFollowing -> currentNumber + 1   // 팔로우 → +1
            isFollowing && !newFollowing -> (currentNumber - 1).coerceAtLeast(0) // 언팔 → -1
            else -> currentNumber
        }

        // 상태 반영
        isFollowing = newFollowing
        saveFollowState(artistIdFromIntent, isFollowing)    // 로컬: 팔로우 상태 저장
        saveFollowerCount(artistIdFromIntent, nextNumber)   // 로컬: 팔로워 수 저장

        // UI 반영
        setFollowerCountText(nextNumber)
        binding.btnFollowing.setBackgroundResource(
            if (isFollowing) R.drawable.pf_follow else R.drawable.pf_unfollow
        )
    }

    private fun showLog(msg: String) {
        Log.d("AuthorProfileActivity", msg)
    }

    private fun showPostScreen(commissionId: Int) {
        // 오버레이 컨테이너 생성(최상단에 덮어쓰기)
        if (composeOverlay == null) {
            composeOverlay = FrameLayout(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(android.graphics.Color.WHITE) // 필요시 반투명/투명 조정
            }
        } else {
            (composeOverlay!!.parent as? ViewGroup)?.removeView(composeOverlay)
        }

        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                val commission by postViewModel.commissionDetail.collectAsState()

                LaunchedEffect(commissionId) {
                    postViewModel.loadCommissionDetail(this@AuthorProfileActivity, commissionId)
                }

                commission?.let { itDetail ->
                    PostScreen(
                        title = itDetail.title,
                        tags = listOf(itDetail.category) + itDetail.tags,
                        minPrice = itDetail.minPrice,
                        summary = itDetail.summary,
                        content = itDetail.content,
                        images = itDetail.images.map { img -> img.imageUrl },
                        isBookmarked = itDetail.isBookmarked,
                        imageCount = itDetail.images.size,
                        currentIndex = 0,
                        commissionId = itDetail.id,
                        onReviewListClick = { /* ... */ },
                        onChatClick = {
                            val intent = Intent(context, MainActivity::class.java).apply {
                                putExtra("openFragment", "chat")
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            }
                            context.startActivity(intent)
                        }
                    )

                }
            }
        }

        composeOverlay!!.removeAllViews()
        composeOverlay!!.addView(composeView)
        (binding.root as ViewGroup).addView(composeOverlay)
    }

    private fun createChatroomFromProfile(commissionId: Int, commissionTitle: String) {
        val api = RetrofitObject.getRetrofitService(this)
        val currentUserId = 1
        val artistId = artistIdFromIntent.takeIf { it != -1 } ?: 1
        val artistName = binding.tvUsername.text?.toString().orEmpty()
        val tempRequestId = 3

        val req = RetrofitClient.CreateChatroomRequest(
            consumerId = currentUserId,
            artistId = artistId,
            requestId = tempRequestId
        )

        api.createChatroom(req).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                if (!response.isSuccessful) {
                    Log.d("AuthorProfileActivity", "채팅방 생성 실패(${response.code()})")
                    return
                }
                val data = response.body()?.success ?: run {
                    Log.d("AuthorProfileActivity", "채팅방 생성 실패(응답 없음)")
                    return
                }

                Log.d("AuthorProfileActivity", "채팅방 생성 성공: ${data.id}")

                val intent = Intent(this@AuthorProfileActivity, MainActivity::class.java).apply {
                    putExtra("openFragment", "postChatDetail")
                    putExtra("chatName", commissionTitle)
                    putExtra("authorName", artistName)
                    putExtra("chatroomId", data.id)
                    putExtra("sourceFragment", "AuthorProfileActivity")
                    putExtra("commissionId", commissionId)
                }
                startActivity(intent)
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.d("AuthorProfileActivity", "네트워크 오류: ${t.message}")
            }
        })
    }
}
