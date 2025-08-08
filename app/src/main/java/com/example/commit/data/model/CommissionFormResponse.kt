package com.example.commit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class CommissionFormResponse(
    val resultType: String,
    val error: String?,
    val success: CommissionFormSuccess?
) : Parcelable

@Parcelize
data class CommissionFormSuccess(
    val commission: CommissionInfo,
    val formSchema: @RawValue Map<String, Any>? = null
) : Parcelable

@Parcelize
data class CommissionInfo(
    val id: Int,
    val title: String,
    val thumbnailImageUrl: String,
    val artist: Artist
) : Parcelable

 