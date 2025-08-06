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

        val photoReviewList = List(5) { R.drawable.sample_review }
        val photoReviewAdapter = PhotoReviewAdapter(this, photoReviewList)
        binding.recyclerReviews.apply {
            adapter = photoReviewAdapter
            layoutManager = LinearLayoutManager(this@ProfileActivity, LinearLayoutManager.HORIZONTAL, false)
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
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        val token = prefs.getString("accessToken", null)

        if (token.isNullOrEmpty()) {
            Log.d("ProfileAPI", "로그인이 필요합니다.")
            return
        }

        val api = RetrofitObject.getRetrofitService(this)
        api.getMyProfile("Bearer $token")
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

        // 신청자 / 작가 태그 표시
        binding.tvBadge.text = if (user.artistId != null) "작가" else "신청자"

        // 프로필 이미지 로드
        Glide.with(this)
            .load(user.profileImage)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(binding.ivProfile)

        // SharedPreferences 저장 (FragmentMypage도 읽을 수 있도록)
        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
        prefs.edit()
            .putString("nickname", user.nickname)
            .putString("imageUri", user.profileImage ?: "") // URL 그대로 저장
            .apply()

        // 배지 표시
        val badgeAdapter = BadgeAdapter(user.badges.map { it.badge.badgeImage }) { badgeUrl ->
            showBadgePopup(badgeUrl)
        }
        binding.recyclerBadges.adapter = badgeAdapter
    }


    private fun updateFollowingCount() {
        binding.btnFollowing.text = "팔로잉 ${followingUsersData.size}"
    }

    private fun showBadgePopup(badgeUrl: String) {
        val popupView = layoutInflater.inflate(R.layout.badge_popup, null)

        popupView.findViewById<TextView>(R.id.tv_badge_popup_text).text = "획득한 배지"
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text2).text = "" // 필요 시 서버에서 조건 내려주면 반영
        Glide.with(this)
            .load(badgeUrl)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile)
            .into(popupView.findViewById<ImageView>(R.id.iv_badge_popup))

        val dialog = Dialog(this).apply {
            setContentView(popupView)
            window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setDimAmount(0.6f)
                setLayout(
                    (resources.displayMetrics.widthPixels - (92 * resources.displayMetrics.density).toInt() * 2),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.CENTER)
            }
        }
        dialog.show()
    }
}
