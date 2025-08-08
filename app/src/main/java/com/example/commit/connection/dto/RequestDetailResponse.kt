package com.example.commit.connection.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import com.google.gson.JsonElement

data class RequestDetailResponse(
    @SerializedName("request") val request: RequestItem,
    @SerializedName("commission") val commission: CommissionItem?,
    @SerializedName("payment") val payment: PaymentInfo?,
    @SerializedName("timeline") val timeline: List<TimelineItem>?,
    @SerializedName("formData") val formData: List<FormItem>?
)

@Parcelize
data class RequestItemDetail(
    @SerializedName("id") val requestId: Int,
    @SerializedName("status") val status: String,
    @SerializedName("totalPrice") val totalPrice: Int,
    @SerializedName("createdAt") val createdAt: String
) : Parcelable

@Parcelize
data class CommissionItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String,
    @SerializedName("artist") val artist: Artist
) : Parcelable

@Parcelize
data class ArtistDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("nickname") val nickname: String
) : Parcelable

@Parcelize
data class PaymentInfo(
    @SerializedName("minPrice") val minPrice: Int,
    @SerializedName("additionalPrice") val additionalPrice: Int,
    @SerializedName("totalPrice") val totalPrice: Int,
    @SerializedName("paidAt") val paidAt: String
) : Parcelable

@Parcelize
data class TimelineItem(
    @SerializedName("status") val status: String,
    @SerializedName("timestamp") val timestamp: String
) : Parcelable


data class FormItem(
    @SerializedName("id") val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("value") val value: JsonElement,
    @SerializedName("type") val type: String
)
