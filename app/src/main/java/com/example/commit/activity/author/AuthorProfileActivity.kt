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
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.adapter.author.AuthorCommissionAdapter
import com.example.commit.adapter.author.AuthorReviewAdapter
import com.example.commit.adapter.mypage.BadgeAdapter
import com.example.commit.data.model.entities.AuthorCommission
import com.example.commit.data.model.entities.AuthorReview
import com.example.commit.databinding.ActivityAuthorProfileBinding
import com.example.commit.databinding.BottomSheetCommissionBinding
import com.example.commit.databinding.BottomSheetHomeBinding
import com.example.commit.databinding.BottomSheetPostMoreBinding
import com.example.commit.fragment.FragmentChat
import com.google.android.material.bottomsheet.BottomSheetDialog

class AuthorProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorProfileBinding
    private var isFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateSlots(remainingSlots = 2, totalSlots = 4)

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

        //배지 리사이클러뷰
        val badgeList = listOf(
            R.drawable.badge_author_1,
            R.drawable.badge_author_5,
            R.drawable.badge_author_15,
            R.drawable.badge_author_50
        )
        val badgeAdapter = BadgeAdapter(badgeList) { showBadgePopup(it) }
        binding.recyclerBadges.apply {
            adapter = badgeAdapter
            layoutManager = LinearLayoutManager(this@AuthorProfileActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        // 커미션 타입 리사이클러뷰
        val commissionList = mutableListOf(
            AuthorCommission(
                R.drawable.image_placeholder, // 썸네일
                "캐릭터 커미션",               // 제목
                "캐릭터를 원하는 스타일로 제작해드립니다.", // 설명
                false,                         // 북마크 여부
                "그림",                        // 태그1
                "#LD",                         // 태그2
                "#커플"                        // 태그3
            ),
            AuthorCommission(
                R.drawable.image_placeholder,
                "배경 일러스트",
                "섬세한 배경 일러스트를 그려드립니다.",
                false,
                "그림",
                "#배경",
                "#풍경"
            ),
            AuthorCommission(
                R.drawable.image_placeholder,
                "아이콘 커미션",
                "아기자기한 아이콘을 제작해드립니다.",
                false,
                "그림",
                "#아이콘",
                "#캐릭터"
            )
        )

        val commissionAdapter = AuthorCommissionAdapter(commissionList)
        binding.recyclerCard.apply {
            adapter = commissionAdapter
            layoutManager = LinearLayoutManager(this@AuthorProfileActivity)
        }


        // 후기 리사이클러뷰 (AuthorReviewAdapter 사용)
        val reviewList = listOf(
            AuthorReview(5.0, "낙서 타임 커미션", "친절하게 응대해주셨습니다. 감사해요!", "위시", "2일 전", "12시간"),
            AuthorReview(4.5, "아이콘 커미션", "퀄리티가 너무 좋아요!", "미루", "5일 전", "1일"),
            AuthorReview(4.8, "배경 일러스트", "정말 만족스러운 결과물이에요.", "하루", "1주 전", "3일")
        )
        val reviewAdapter = AuthorReviewAdapter(reviewList)
        binding.recyclerReviews.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(this@AuthorProfileActivity)
        }
    }

    private fun showBadgePopup(badgeResId: Int) {
        val badgeCount = when (badgeResId) {
            R.drawable.badge_author_50 -> 50
            R.drawable.badge_author_15 -> 15
            R.drawable.badge_author_5 -> 5
            else -> 1
        }

        val badgeLevel = when {
            badgeCount >= 50 -> "다이아"
            badgeCount >= 15 -> "금"
            badgeCount >= 5 -> "은"
            else -> "동"
        }

        val popupView = layoutInflater.inflate(R.layout.badge_popup, null)
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text).text = "커미션 완료 배지 ($badgeLevel)"
        popupView.findViewById<TextView>(R.id.tv_badge_popup_text2).text = "조건 : 커미션 완료 ${badgeCount}회 달성"
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
}
