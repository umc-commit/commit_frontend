package com.example.commit.activity.mypage

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.adapter.mypage.BadgeAdapter
import com.example.commit.adapter.mypage.PhotoReviewAdapter
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.entities.FollowingUser
import com.example.commit.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var isReviewOn = false
    private lateinit var profileEditLauncher: ActivityResultLauncher<Intent>
    private lateinit var latestUserBadges: List<RetrofitClient.UserBadge>

    private val followingUsersData = listOf(
        FollowingUser(R.drawable.ic_pf_charac2, "키르", 32, true),
        FollowingUser(R.drawable.ic_pf_charac2, "곤", 15, true),
        FollowingUser(R.drawable.ic_pf_charac2, "레오리오", 20, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateFollowingCount()
        loadProfileFromApi()

        binding.ivBack.setOnClickListener { finish() }

        profileEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val nickname = result.data?.getStringExtra("nickname")
                val imageUriString = result.data?.getStringExtra("imageUri")
                val intro = result.data?.getStringExtra("intro")
                val prefs = getSharedPreferences("auth", MODE_PRIVATE)

                prefs.edit().apply {
                    if (!nickname.isNullOrEmpty()) putString("nickname", nickname)
                    if (!imageUriString.isNullOrEmpty()) putString("imageUri", imageUriString)
                    if (!intro.isNullOrEmpty()) putString("intro", intro)
                    apply()
                }
            }
        }

        binding.tvBadgeSetting.paintFlags = binding.tvBadgeSetting.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.ivReviewBtn.setOnClickListener {
            isReviewOn = !isReviewOn
            binding.ivReviewBtn.setImageResource(
                if (isReviewOn) R.drawable.iv_review_on else R.drawable.iv_review_off
            )
        }

        binding.btnEditProfile.setOnClickListener {
            profileEditLauncher.launch(Intent(this, ProfileEditActivity::class.java))
        }

        binding.btnFollowing.setOnClickListener {
            startActivity(Intent(this, ProfileFollowingActivity::class.java))
        }

        /*
        val badgeList = listOf(
            R.drawable.badge_applicant_1,
            R.drawable.badge_applicant_5,
            R.drawable.badge_applicant_15,
            R.drawable.badge_applicant_50
        )
        val badgeAdapter = BadgeAdapter(badgeList) { showBadgePopup(it) }
        binding.recyclerBadges.apply {
            adapter = badgeAdapter
            layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.HORIZONTAL, false)
        }
        */
    }

    override fun onResume() {
        super.onResume()
        loadProfileFromApi()
    }

    private fun loadProfileFromApi() {
        val api = RetrofitObject.getRetrofitService(this)

        api.getMyProfile()
            .enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>,
                    response: Response<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body?.resultType == "SUCCESS") {
                            val user = body.success?.user
                            updateProfileUIFromServer(user)
                        } else {
                            Log.d("ProfileAPI", body?.error?.reason ?: "프로필 불러오기 실패")
                        }
                    } else {
                        Log.d("ProfileAPI", "서버 오류: ${response.code()}")
                    }
                }

                override fun onFailure(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>,
                    t: Throwable
                ) {
                    Log.d("ProfileAPI", "네트워크 오류: ${t.message}")
                }
            })
    }


    private fun updateProfileUIFromServer(user: RetrofitClient.UserProfile?) {
        if (user == null) return

        binding.tvUsername.text = user.nickname
        binding.tvIntroContent.text = user.description ?: "입력된 소개가 없습니다."
        binding.tvBadge.text = if (user.artistId != null) "작가" else "신청자"

        Glide.with(this)
            .load(user.profileImage?.takeIf { it.isNotBlank() } ?: R.drawable.ic_profile)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(binding.ivProfile)

        // 배지 어댑터
        binding.recyclerBadges.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        latestUserBadges = user.badges
        val badgeAdapter = BadgeAdapter(badgeList = user.badges, context = this) { dialog -> dialog.show() }
        binding.recyclerBadges.adapter = badgeAdapter

        // 포토후기 어댑터: 서버 리뷰의 썸네일만 필터링 후 세팅
        val thumbnailReviews = user.reviews.filter { !it.reviewThumbnail.isNullOrBlank() }
        binding.recyclerReviews.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = PhotoReviewAdapter(
                context = this@ProfileActivity,
                items = thumbnailReviews,
                reviewerName = user.nickname // PhotoReview 화면에서 사용
            )
        }
    }

    private fun updateFollowingCount() {
        binding.btnFollowing.text = "팔로잉 ${followingUsersData.size}"
    }
}
