package com.example.commit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommissionRequestResponse(
    val resultType: String,
    val error: String?,
    val success: CommissionRequestSuccess?
) : Parcelable

@Parcelize
data class CommissionRequestSuccess(
    val request_id: Int,
    val status: String,
    val created_at: String
) : Parcelable 