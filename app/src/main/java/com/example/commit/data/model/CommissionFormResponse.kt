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
    val formSchema: FormSchema? = null
) : Parcelable

@Parcelize
data class CommissionInfo(
    val id: Int,
    val title: String,
    val thumbnailImageUrl: String,
    val artist: Artist
) : Parcelable

@Parcelize
data class FormSchema(
    val fields: List<FormField>? = null
) : Parcelable

@Parcelize
data class FormField(
    val id: String,
    val type: String,
    val label: String,
    val required: Boolean = false,
    val options: List<FormOption>? = null,
    val maxLength: Int? = null,
    val maxFiles: Int? = null,
    val acceptedTypes: List<String>? = null
) : Parcelable

@Parcelize
data class FormOption(
    val value: String,
    val label: String,
    val additionalPrice: Int = 0
) : Parcelable

 