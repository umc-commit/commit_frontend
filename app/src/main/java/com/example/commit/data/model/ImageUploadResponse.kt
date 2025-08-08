package com.example.commit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUploadResponse(
    val resultType: String,
    val error: String?,
    val success: ImageUploadSuccess?
) : Parcelable

@Parcelize
data class ImageUploadSuccess(
    val image_url: String,
    val file_size: Long,
    val file_type: String
) : Parcelable 