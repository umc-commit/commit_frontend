package com.example.commit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
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
) : Parcelable

@Parcelize
data class Artist(
    val id: Int,
    val nickname: String
) : Parcelable

@Parcelize
data class Commission(
    val id: Int
) : Parcelable
