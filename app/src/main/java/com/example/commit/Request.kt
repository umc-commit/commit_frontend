package com.example.commit

data class Request(
    val requestId: Int,
    val status: String,
    val title: String,
    val price: Int,
    val thumbnailImage: String,
    val artist: Artist
)

data class Artist(
    val id: Int,
    val nickname: String
)