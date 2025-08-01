package com.example.commit.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Artist(
    val id: Int,
    val nickname: String
) : Parcelable

@Parcelize
data class RequestItem(
    val requestId: Int,
    val status: String,
    val title: String,
    val price: Int,
    val thumbnailImage: String,
    val artist: Artist,
    val createdAt: String = ""
) : Parcelable