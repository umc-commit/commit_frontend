package com.example.commit.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.R
import com.example.commit.adapter.PhotoReviewAdapter
import com.example.commit.databinding.ActivityProfileBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.adapter.BadgeAdapter
import com.example.commit.data.model.entities.FollowingUser

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var isReviewOn = false // 이미지 상태 저장 변수

    // 실제 앱에서는 이 데이터를 서버나 로컬 DB에서 가져오게 됩니다.
    private val followingUsersData = listOf(
        FollowingUser(R.drawable.ic_pf_charac, "키르", 32, true),
        FollowingUser(R.drawable.ic_pf_charac, "곤", 15, true),
        FollowingUser(R.drawable.ic_pf_charac, "레오리오", 20, true)
        // 여기에 더미 데이터를 추가하거나, 실제 데이터를 로드하는 로직으로 대체하세요.
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateFollowingCount()

        binding.ivBack.setOnClickListener {
            finish()   // 이전 화면으로 돌아가기
        }

        // 포토후기 RecyclerView 어댑터 연결 (더미 이미지 5개)
        val photoReviewList = listOf(
            R.drawable.sample_review,
            R.drawable.sample_review,
            R.drawable.sample_review,
            R.drawable.sample_review,
            R.drawable.sample_review
        )
        val photoReviewAdapter = PhotoReviewAdapter(this, photoReviewList)
        binding.recyclerReviews.adapter = photoReviewAdapter

        // 반드시 LayoutManager를 먼저 설정!
        binding.recyclerReviews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 배지 설정 밑줄
        binding.tvBadgeSetting.paintFlags = binding.tvBadgeSetting.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.ivReviewBtn.setOnClickListener {
            isReviewOn = !isReviewOn
            if (isReviewOn) {
                binding.ivReviewBtn.setImageResource(R.drawable.iv_review_on)
            } else {
                binding.ivReviewBtn.setImageResource(R.drawable.iv_review_off)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        binding.btnFollowing.setOnClickListener {
            val intent = Intent(this, ProfileFollowingActivity::class.java)
            startActivity(intent)
        }

        val badgeList = listOf(
            R.drawable.badge_applicant_1,
            R.drawable.badge_applicant_5,
            R.drawable.badge_applicant_15,
            R.drawable.badge_applicant_50
        )

        val badgeAdapter = BadgeAdapter(badgeList) { badgeResId ->
            // 클릭 시 배지 팝업 띄우기
            showBadgePopup(badgeResId)
        }

        binding.recyclerBadges.adapter = badgeAdapter
        binding.recyclerBadges.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onResume() {
        super.onResume()
        // ProfileFollowingActivity에서 돌아왔을 때 등, 화면에 다시 나타날 때마다 팔로잉 수 업데이트
        updateFollowingCount()
    }

    // 팔로잉 수를 업데이트하는 함수s
    private fun updateFollowingCount() {
        val count = followingUsersData.size // 데이터 리스트의 실제 개수를 가져옴
        binding.btnFollowing.text = "팔로잉 $count" // 버튼 텍스트 업데이트
    }

    private fun showBadgePopup(badgeResId: Int) {
        val badgeCount = when (badgeResId) {
            R.drawable.badge_applicant_50 -> 50
            R.drawable.badge_applicant_15 -> 15
            R.drawable.badge_applicant_5 -> 5
            else -> 1
        }

        val badgeLevel = when {
            badgeCount >= 50 -> "다이아"
            badgeCount >= 15 -> "금"
            badgeCount >= 5 -> "은"
            else -> "동"
        }

        val popupView = layoutInflater.inflate(R.layout.badge_popup, null)
        val tvBadgeText = popupView.findViewById<TextView>(R.id.tv_badge_popup_text)
        val tvBadgeDesc = popupView.findViewById<TextView>(R.id.tv_badge_popup_text2)
        val ivBadgePopup = popupView.findViewById<ImageView>(R.id.iv_badge_popup)

        tvBadgeText.text = "커미션 신청 배지 ($badgeLevel)"
        tvBadgeDesc.text = "조건 : 커미션 신청 횟수 ${badgeCount}회 달성"
        ivBadgePopup.setImageResource(badgeResId)

        val dialog = Dialog(this)
        dialog.setContentView(popupView)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginPx = (92 * displayMetrics.density).toInt()
        val targetWidth = screenWidth - (marginPx * 2)

        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent) // 둥근 배경 살리기
            setDimAmount(0.6f) // 배경 어둡게
            setLayout(
                targetWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER) // 중앙 정렬
        }
        dialog.show()
    }

} 