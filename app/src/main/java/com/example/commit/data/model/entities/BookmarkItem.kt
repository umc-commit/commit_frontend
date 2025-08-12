package com.example.commit.data.model.entities

data class BookmarkItem(
    val id: Int,
    val nickname: String,
    val profileImageUrl: String,
    val thumbnailUrl: String,
    val title: String,
    val tags: List<String>,
    val remainingSlots: Int,
    val isClosed: Boolean,
    val date: String,  // 날짜 — 최신순 테스트용
    val price: Int,       // 가격 — 저가순/고가순 테스트용
    var isSelected: Boolean = false
)
