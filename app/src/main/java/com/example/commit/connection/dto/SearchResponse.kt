package com.example.commit.connection.dto

import com.google.gson.annotations.SerializedName

data class ErrorData(
    @SerializedName("reason") val reason: String?
)

// /api/search 성공 페이로드
data class SearchSuccess(
    @SerializedName("commissions") val commissions: List<CommissionSummary>,
    @SerializedName("pagination") val pagination: Pagination
)

data class CommissionSummary(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("minPrice") val minPrice: Int,
    @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String?,
    @SerializedName("deadline") val deadline: Int?,
    @SerializedName("isBookmarked") val isBookmarked: Boolean,
    @SerializedName("bookmarkId") val bookmarkId: Long?,
    @SerializedName("category") val category: CategoryDto,
    @SerializedName("artist") val artist: ArtistDto,
    @SerializedName("tags") val tags: List<TagDto>,
)

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class ArtistDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)

data class TagDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)
