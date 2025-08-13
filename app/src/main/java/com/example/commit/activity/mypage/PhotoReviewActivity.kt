package com.example.commit.activity.mypage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.databinding.ActivityPhotoReviewBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class PhotoReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        // 인텐트에서 값 수신
        val imageUrl = intent.getStringExtra("reviewImageUrl")
        val rate = intent.getIntExtra("rate", 0)
        val content = intent.getStringExtra("content") ?: ""
        val createdAt = intent.getStringExtra("createdAt") ?: ""
        val reviewerName = intent.getStringExtra("reviewerName") ?: "익명의 신청자"
        val requestId = intent.getStringExtra("requestId") ?: ""

        // 이미지
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.sample_review)
            .error(R.drawable.sample_review)
            .fallback(R.drawable.sample_review)
            .centerCrop()
            .into(binding.ivReviewImage)

        // 별점
        binding.tvRating.text = String.format("%.1f", rate.toDouble())

        // 커미션 타입 텍스트(타이틀 없으니 임시)
        binding.tvCommissionType.text = if (requestId.isNotBlank()) "낙서 타입 커미션" else "커미션"

        // 작성일 포맷: 2025.06.02 형식
        binding.tvReviewDate.text = formatDate(createdAt)

        // 작성자
        binding.tvReviewer.text = reviewerName

        // 내용
        binding.tvReviewContent.text = content
    }

    private fun formatDate(isoString: String): String {
        return try {
            val parser = if (isoString.contains(".")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            }
            parser.timeZone = TimeZone.getTimeZone("UTC") // Z(UTC) 처리

            val date = parser.parse(isoString)
            val formatter = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            formatter.format(date)
        } catch (e: Exception) {
            isoString // 실패 시 원문 그대로
        }
    }
}
