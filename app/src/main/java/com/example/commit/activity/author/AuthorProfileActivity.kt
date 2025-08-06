package com.example.commit.activity.author

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.example.commit.databinding.BottomSheetCommissionBinding
import com.example.commit.databinding.BottomSheetHomeBinding
import com.example.commit.databinding.BottomSheetPostMoreBinding
import com.example.commit.fragment.FragmentChat
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorProfileBinding
    private var isFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val artistId = intent.getIntExtra("artistId", -1)
        if (artistId != -1) {
            loadAuthorProfile(artistId)
        }


        binding.ivBack.setOnClickListener { finish() }
        binding.ivMore.setOnClickListener { showSortBottomSheet() }

        // 후기 펼침/접힘 상태 변수
        var isReviewExpanded = false

        // 초기에 리뷰 목록 숨기기 (원하면 주석 처리)
        binding.recyclerReviews.visibility = View.GONE
        binding.ivDropdown.setOnClickListener {
            isReviewExpanded = !isReviewExpanded
            binding.ivDropdown.setImageResource(
                if (isReviewExpanded) R.drawable.ic_dropup else R.drawable.ic_dropdown
            )
            toggleReviewSection(isReviewExpanded)
        }

        //팔로잉 버튼
        binding.btnFollowing.setOnClickListener {
            isFollowing = !isFollowing

            // 현재 팔로워 숫자 추출
            val currentText = binding.tvFollowerCount.text.toString() // 예: "팔로워 32"
            val currentNumber = currentText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0

            // 팔로잉 상태에 따라 숫자 증가/감소
            val newNumber = if (isFollowing) {
                currentNumber + 1
            } else {
                (currentNumber - 1).coerceAtLeast(0) // 음수 방지
            }

            // UI 갱신
            binding.tvFollowerCount.text = "팔로워 $newNumber"

            if (isFollowing) {binding.btnFollowing.setBackgroundResource(R.drawable.pf_follow)}
            else {binding.btnFollowing.setBackgroundResource(R.drawable.pf_unfollow)}
        }

        //채팅 버튼
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "chat")
            startActivity(intent)
            finish() // 프로필 화면 닫기 (원하면 유지 가능)
        }
    }

    private fun showBadgePopup(badgeUrl: String, badgeThreshold: Int) {
        // threshold 값으로 배지 등급 계산
        val badgeLevel = when {
            badgeThreshold >= 50 -> "다이아"
            badgeThreshold >= 15 -> "금"
            badgeThreshold >= 5 -> "은"
            else -> "동"
        }

        val popupView = layoutInflater.inflate(R.layout.badge_popup, null)

        // 텍스트 세팅
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text)
            .text = "커미션 완료 배지 ($badgeLevel)"
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text2)
            .text = "조건 : 커미션 완료 ${badgeThreshold}회 달성"

        // URL 이미지 Glide로 로드
        Glide.with(this)
            .load(badgeUrl)
            .placeholder(R.drawable.ic_profile) // 로딩 중 기본 이미지
            .error(R.drawable.ic_profile) // 실패 시 기본 이미지
            .into(popupView.findViewById(R.id.iv_badge_popup))

        // 팝업 다이얼로그 생성
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

    private fun showSortBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val sheetBinding = BottomSheetPostMoreBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(sheetBinding.root)

        // 배경 투명 & 그림자 효과
        bottomSheetDialog.window?.apply {
            setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
            setDimAmount(0.6f)
        }

        bottomSheetDialog.show()

    }

    private fun updateSlots(remainingSlots: Int, totalSlots: Int = 4) {
        // 슬롯 이미지뷰 리스트
        val slotIcons = listOf(
            binding.slot1,
            binding.slot2,
            binding.slot3,
            binding.slot4
        )

        // 채워진 슬롯 개수 계산
        val filledCount = totalSlots - remainingSlots

        // 채워진 슬롯
        for (i in 0 until filledCount) {
            if (i < slotIcons.size) {
                slotIcons[i].setImageResource(R.drawable.ic_slot_filled)
            }
        }

        // 비어있는 슬롯
        for (i in filledCount until totalSlots) {
            if (i < slotIcons.size) {
                slotIcons[i].setImageResource(R.drawable.ic_slot_empty)
            }
        }
        // 남은 슬롯 텍스트 업데이트
        binding.tvRemainingSlots.text = "남은 슬롯 ${remainingSlots}개"
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
                .withEndAction {
                    binding.recyclerReviews.visibility = View.GONE
                }
                .start()
        }
    }

    private fun loadAuthorProfile(artistId: Int) {
        val service = RetrofitObject.getRetrofitService(this)
        val token = getSharedPreferences("auth", MODE_PRIVATE)
            .getString("accessToken", null) ?: return

        service.getAuthorProfile("Bearer $token", artistId)
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
                        .load(data.profileImage)
                        .placeholder(R.drawable.ic_profile)
                        .into(binding.ivProfile)
                    updateSlots(data.slot)

                    // 배지
                    binding.recyclerBadges.layoutManager =
                        LinearLayoutManager(this@AuthorProfileActivity, LinearLayoutManager.HORIZONTAL, false)
                    val badgeList = data.badges
                    binding.recyclerBadges.adapter =
                        BadgeAdapter(badgeList.map { it.badge.badgeImage }) { url ->
                            val badgeData = badgeList.firstOrNull { it.badge.badgeImage == url }
                            showBadgePopup(url, badgeData?.badge?.threshold ?: 1)
                        }

                    // 커미션
                    binding.recyclerCard.layoutManager = LinearLayoutManager(this@AuthorProfileActivity)
                    binding.recyclerCard.adapter =
                        AuthorCommissionAdapter(data.commissions.toMutableList())

                    // 리뷰
                    binding.recyclerReviews.layoutManager = LinearLayoutManager(this@AuthorProfileActivity)
                    binding.recyclerReviews.adapter =
                        AuthorReviewAdapter(data.reviews)
                }

                override fun onFailure(call: Call<RetrofitClient.AuthorProfileResponse>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }
}
