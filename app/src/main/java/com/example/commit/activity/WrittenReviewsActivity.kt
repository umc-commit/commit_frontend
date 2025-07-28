package com.example.commit.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.commit.data.model.entities.Review
import com.example.commit.ui.review.ReviewListScreen

class WrittenReviewsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✨ 더미 데이터 (나중에 ViewModel로 교체 가능)
        val reviewList = listOf(
            Review(
                id = 1,
                nickname = "키르",
                title = "낙서 타입 커미션",
                content = "요청사항도 잘 들어주시고 마감도 빠르게 해주셨어요! 감사합니다.",
                duration = "작업기간 : 23시간",
                rating = 4
            ),
            Review(
                id = 2,
                nickname = "키르",
                title = "낙서 타입 커미션",
                content = "요청사항도 잘 들어주시고 마감도 빠르게 해주셨어요! 감사합니다.",
                duration = "작업기간 : 20시간",
                rating = 3
            )
        )

        setContent {
            var reviews by remember { mutableStateOf(reviewList) }

            ReviewListScreen(
                reviews = reviews,
                onBackClick = { finish() },
                onEditClick = { /* TODO: 수정 화면 이동 */ },
                onDeleteClick = { reviewToDelete ->
                    reviews = reviews.filterNot { it.id == reviewToDelete.id }
                }
            )
        }
    }
}
