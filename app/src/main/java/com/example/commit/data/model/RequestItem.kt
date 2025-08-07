package com.example.commit.data.model

data class RequestItem(
    val requestId: Int,
    val status: String,
    val title: String,
    val price: Int,
    val thumbnailImageUrl: String,
    val progressPercent: Int,
    val createdAt: String,
    val artist: Artist,
    val commission: Commission
)

data class Artist(
    val id: Int,
    val nickname: String
)

data class Commission(
    val id: Int
)
