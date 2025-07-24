package com.example.commit.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.R
import com.example.commit.adapter.PhotoReviewAdapter
import com.example.commit.databinding.ActivityProfileBinding
import androidx.recyclerview.widget.LinearLayoutManager
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

    }

    override fun onResume() {
        super.onResume()
        // ProfileFollowingActivity에서 돌아왔을 때 등, 화면에 다시 나타날 때마다 팔로잉 수 업데이트
        updateFollowingCount()
    }

    // 팔로잉 수를 업데이트하는 함수
    private fun updateFollowingCount() {
        val count = followingUsersData.size // 데이터 리스트의 실제 개수를 가져옴
        binding.btnFollowing.text = "팔로잉 $count" // 버튼 텍스트 업데이트
    }
} 