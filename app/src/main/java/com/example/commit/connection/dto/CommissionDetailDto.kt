package com.example.commit.connection.dto

import com.google.gson.annotations.SerializedName

// 커미션 상세 응답 데이터
data class CommissionDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("minPrice")
    val minPrice: Int,
    @SerializedName("category")
    val category: String,
    @SerializedName("tags")
    val tags: List<String>,
    @SerializedName("images")
    val images: List<CommissionImage>,
    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String,
    @SerializedName("remainingSlots")
    val remainingSlots: Int,
    @SerializedName("isBookmarked")
    val isBookmarked: Boolean,
    @SerializedName("bookmarkId")
    val bookmarkId: Long?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("artistId")
    val artistId: Int
)

data class CommissionImage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageUrl")
    val imageUrl: String,
    @SerializedName("orderIndex")
    val orderIndex: Int
)


data class CommissionDetailResponse(
    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("error")
    val error: String?,
    @SerializedName("success")
    val success: CommissionDetail?
)