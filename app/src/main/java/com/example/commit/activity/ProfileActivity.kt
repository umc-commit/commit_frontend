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

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var isReviewOn = false // 이미지 상태 저장 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }
} 