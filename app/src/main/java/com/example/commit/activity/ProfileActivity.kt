package com.example.commit.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.adapter.BadgeAdapter
import com.example.commit.adapter.PhotoReviewAdapter
import com.example.commit.data.model.entities.FollowingUser
import com.example.commit.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var isReviewOn = false
    private lateinit var profileEditLauncher: ActivityResultLauncher<Intent>

    private val followingUsersData = listOf(
        FollowingUser(R.drawable.ic_pf_charac, "키르", 32, true),
        FollowingUser(R.drawable.ic_pf_charac, "곤", 15, true),
        FollowingUser(R.drawable.ic_pf_charac, "레오리오", 20, true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateProfileUI()
        updateFollowingCount()

        binding.ivBack.setOnClickListener { finish() }

        profileEditLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val nickname = result.data?.getStringExtra("nickname")
                val imageUriString = result.data?.getStringExtra("imageUri")
                val intro = result.data?.getStringExtra("intro")
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)

                prefs.edit().apply {
                    if (!nickname.isNullOrEmpty()) putString("nickname", nickname)
                    if (!imageUriString.isNullOrEmpty()) putString("imageUri", imageUriString)
                    if (!intro.isNullOrEmpty()) putString("intro", intro)
                    apply()
                }

                updateProfileUI()
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
    }

    override fun onResume() {
        super.onResume()
        updateFollowingCount()
    }

    private fun updateProfileUI() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val nickname = prefs.getString("nickname", "로지")
        val imageUriString = prefs.getString("imageUri", null)
        val intro = prefs.getString("intro", "입력된 소개가 없습니다.")

        binding.tvUsername.text = nickname
        binding.tvIntroContent.text = intro

        if (!imageUriString.isNullOrEmpty()) {
            val uri = Uri.parse(imageUriString)
            Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_profile)
        }
    }

    private fun updateFollowingCount() {
        binding.btnFollowing.text = "팔로잉 ${followingUsersData.size}"
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
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text).text = "커미션 신청 배지 ($badgeLevel)"
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text2).text = "조건 : 커미션 신청 횟수 ${badgeCount}회 달성"
        popupView.findViewById<ImageView>(R.id.iv_badge_popup).setImageResource(badgeResId)

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
