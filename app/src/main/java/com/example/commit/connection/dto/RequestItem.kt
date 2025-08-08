package com.example.commit.connection.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestItem(
    @SerializedName("requestId")
    val requestId: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("thumbnailImageUrl")
    val thumbnailImageUrl: String,

    @SerializedName("progressPercent")
    val progressPercent: Int,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("artist")
    val artist: Artist,

    @SerializedName("commission")
    val commission: Commission
) : Parcelable

@Parcelize
data class Artist(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nickname")
    val nickname: String
) : Parcelable

@Parcelize
data class Commission(
    @SerializedName("id")
    val id: Int
) : Parcelable
