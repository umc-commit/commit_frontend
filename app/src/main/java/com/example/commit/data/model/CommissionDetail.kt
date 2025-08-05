package com.example.commit.data.model

data class CommissionDetailResponse(
    val resultType: String,
    val error: String?,
    val success: CommissionDetail?
)

data class CommissionDetail(
    val id: Int,
    val title: String,
    val summary: String,
    val content: String,
    val minPrice: Int,
    val category: String,
    val tags: List<String>,
    val images: List<CommissionImage>,
    val thumbnailImageUrl: String,
    val remainingSlots: Int,
    val isBookmarked: Boolean,
    val createdAt: String
)

data class CommissionImage(
    val id: Int,
    val imageUrl: String,
    val orderIndex: Int
)