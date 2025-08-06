package com.example.commit.data.model

data class ArtistInfoResponse(
    val resultType: String,
    val error: ErrorData?,
    val success: ArtistSuccessData?
)

data class ArtistSuccessData(
    val artist: ArtistInfo,
    val isFollowing: Boolean,
    val reviewStatistics: ReviewStatistics,
    val recentReviews: List<Review>,
    val pagination: Pagination
)

data class ArtistInfo(
    val artistId: Int,
    val nickname: String,
    val profileImageUrl: String,
    val follower: Int,
    val completedworks: Int
)

data class ReviewStatistics(
    val averageRate: Float,
    val totalReviews: Int,
    val recommendationRate: Int
)

data class Review(
    val id: Int,
    val rate: Int,
    val content: String,
    val userNickname: String,
    val commissionTitle: String,
    val workperiod: String,
    val createdAt: String,
    val timeAgo: String,
    val images: List<ReviewImage>
)

data class ReviewImage(
    val id: Int,
    val imageUrl: String,
    val orderIndex: Int
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class ErrorData(
    val reason: String
)
