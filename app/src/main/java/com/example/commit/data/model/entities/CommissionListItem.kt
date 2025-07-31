package com.example.commit.data.model.entities

import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

// 완료된 커미션 항목 정보를 담을 데이터 클래스
data class CommissionListItem(
    val date: Date, // 날짜를 java.util.Date 타입으로 변경
    val thumbnailResId: Int, // 커미션 썸네일 이미지 리소스 ID
    val title: String, // 커미션 제목 (예: 닉네임)
    val description: String, // 커미션 설명 (예: 낙서 타입 커미션)
    val price: Int, // 가격 (원 단위, P 제외)
    val status: String // 거래 상태 (예: 거래완료)
) {
    // 가격을 "40,000P" 형식으로 포맷팅하는 헬퍼 함수
    fun getFormattedPrice(): String {
        return String.format("%,dP", price)
    }

    // 날짜를 "yy.MM.dd" 형식으로 포맷팅하는 헬퍼 함수
    fun getFormattedDate(): String {
        // SimpleDateFormat을 사용하여 날짜 포맷팅
        val formatter = SimpleDateFormat("yy.MM.dd", Locale.getDefault()) // 지역 기본값 사용
        return formatter.format(date)
    }
}

