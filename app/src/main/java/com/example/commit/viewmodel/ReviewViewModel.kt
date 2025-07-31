package com.example.commit.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.commit.data.model.entities.Review

class ReviewViewModel : ViewModel() {

    // 리뷰 리스트 상태
    var reviewList by mutableStateOf(listOf<Review>())
        private set

    // 선택된 리뷰 (삭제/수정 시 사용)
    var selectedReview by mutableStateOf<Review?>(null)
        private set

    init {
        // 테스트용 초기값
        reviewList = listOf(
            Review(1, "키르", "낙서 타입 커미션", "요청사항도 잘 들어주시고 마감도 빠르게 해주셨어요! 감사합니다.", "23시간", 5),
            Review(2, "루카", "풀컬러 커미션", "감사합니다ㅠㅠ 너무 귀여워요", "2일", 4)
        )
    }

    fun addReview(newReview: Review) {
        reviewList = reviewList + newReview
    }

    fun deleteReview(targetId: Int) {
        reviewList = reviewList.filterNot { it.id == targetId }
    }

    fun updateReview(updated: Review) {
        reviewList = reviewList.map {
            if (it.id == updated.id) updated else it
        }
    }

    fun selectReview(review: Review?) {
        selectedReview = review
    }
}
