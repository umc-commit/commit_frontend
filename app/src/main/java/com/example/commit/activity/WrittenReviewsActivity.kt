package com.example.commit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.adapter.ReviewAdapter
import com.example.commit.data.model.entities.Review

class WrittenReviewsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        val recyclerView = findViewById<RecyclerView>(R.id.reviewsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 임시 더미 데이터
        val reviewList = listOf(
            Review(
                title = "낙서 타입 커미션",
                content = "요청사항도 잘 들어주시고 마감도 빠르게 해주셨어요! 감사합니다.",
                nickname = "키르",
                duration = "작업기간 : 23시간"
            ),
            Review(
                title = "캐릭터 커미션",
                content = "친절하고 작업도 너무 만족스러웠어요!! 다음에 또 부탁드리고 싶어요 🥺",
                nickname = "하루",
                duration = "작업기간 : 1일"
            )
        )

        recyclerView.adapter = ReviewAdapter(reviewList)
    }
}
