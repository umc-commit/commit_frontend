package com.example.commit.connection.dto

data class CommissionArtistResponse(
    val artist: ArtistSummary,
    val isFollowing: Boolean,
    val reviewStatistics: ReviewStatistics,
    val recentReviews: List<ArtistRecentReview>,
    val pagination: Pagination
)

data class ArtistSummary(
    val artistId: Long,
    val nickname: String,
    val profileImageUrl: String?,
    val follower: Int,
    val completedworks: Int
)

data class ReviewStatistics(
    val averageRate: Int,
    val totalReviews: Int,
    val recommendationRate: Int
)

data class ArtistRecentReviewImage(
    val id: Long,
    val imageUrl: String,
    val orderIndex: Int
)

data class ArtistRecentReview(
    val id: Long,
    val rate: Int,
    val content: String,
    val userNickname: String,
    val commissionTitle: String,
    val workperiod: String,
    val createdAt: String,
    val timeAgo: String,
    val images: List<ArtistRecentReviewImage>
)


