package com.example.commit.data.model

data class Artist(
    val id: Int,
    val nickname: String
)

data class RequestItem(
    val requestId: Int,
    val status: String,
    val title: String,
    val price: Int,
    val thumbnailImage: String,
    val artist: Artist
)
