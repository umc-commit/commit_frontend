package com.example.commit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.R
import com.example.commit.databinding.ActivityPhotoReviewBinding

class PhotoReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()   // 이전 화면으로 돌아가기
        }
    }
} 