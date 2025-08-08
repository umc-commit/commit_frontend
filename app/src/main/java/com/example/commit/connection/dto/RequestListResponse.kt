package com.example.commit.connection.dto

import com.google.gson.annotations.SerializedName

data class RequestListResponse(
    @SerializedName("requests")
    val requests: List<RequestItem>,

    @SerializedName("pagination")
    val pagination: Pagination
)

data class Pagination(
    @SerializedName("page")
    val page: Int,

    @SerializedName("limit")
    val limit: Int,

    @SerializedName("totalCount")
    val totalCount: Int,

    @SerializedName("totalPages")
    val totalPages: Int
)
