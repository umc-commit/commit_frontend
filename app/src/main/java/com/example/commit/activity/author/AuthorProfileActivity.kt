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
import com.example.commit.databinding.BottomSheetPostMoreBinding
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

        // 기본 RecyclerView 초기화 (빈 어댑터)
        initRecyclerViews()

        val artistId = intent.getIntExtra("artistId", -1)
        if (artistId != -1) {
            loadAuthorProfile(artistId)
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

        // 팔로잉 버튼
        binding.btnFollowing.setOnClickListener {
            isFollowing = !isFollowing
            updateFollowingCount()
        }

        // 채팅 버튼
        binding.btnChat.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openFragment", "chat")
            startActivity(intent)
            finish()
        }
    }

    private fun initRecyclerViews() {
        // 배지 RecyclerView
        binding.recyclerBadges.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // 커미션 RecyclerView
        binding.recyclerCard.layoutManager = LinearLayoutManager(this)
        binding.recyclerCard.adapter = AuthorCommissionAdapter(mutableListOf())

        // 리뷰 RecyclerView
        binding.recyclerReviews.layoutManager = LinearLayoutManager(this)
        binding.recyclerReviews.adapter = AuthorReviewAdapter(emptyList())
    }

    private fun updateFollowingCount() {
        val currentText = binding.tvFollowerCount.text.toString()
        val currentNumber = currentText.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
        val newNumber = if (isFollowing) currentNumber + 1 else (currentNumber - 1).coerceAtLeast(0)

        binding.tvFollowerCount.text = "팔로워 $newNumber"
        binding.btnFollowing.setBackgroundResource(
            if (isFollowing) R.drawable.pf_follow else R.drawable.pf_unfollow
        )
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
                    val convertedBadges = data.badges.map {
                        val badgeDetail = RetrofitClient.BadgeDetail(
                            id = it.badge.id,
                            type = it.badge.type,
                            threshold = it.badge.threshold,
                            name = it.badge.name,
                            badgeImage = it.badge.badgeImage
                        )
                        RetrofitClient.UserBadge(
                            id = it.id,
                            earnedAt = it.earnedAt,
                            badge = badgeDetail
                        )
                    }

                    binding.recyclerBadges.adapter =
                        BadgeAdapter(
                            badgeList = convertedBadges,
                            context = this@AuthorProfileActivity
                        ) { dialog ->
                            dialog.show()
                        }


                    // 커미션
                    binding.recyclerCard.adapter =
                        AuthorCommissionAdapter(data.commissions.toMutableList())

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
}
