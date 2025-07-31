package com.example.commit.data.model.entities

data class AuthorCommission(
    val thumbnailResId: Int,   // 이미지 리소스 (또는 URL)
    val title: String,         // 커미션 제목
    val description: String,   // 설명
    var isBookmarked: Boolean, // 북마크 상태
    val tagDrawing: String,    // 태그1
    val tagLD: String,         // 태그2
    val tagCouple: String      // 태그3
)
