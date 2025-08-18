package com.example.commit.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TimelineItem(
    val status: String,
    val label: String,
    val changedAt: String
) : Parcelable

@Parcelize
data class PaymentInfo(
    val paidAt: String,
    val basePrice: Int,
    val additionalPrice: Int,
    val totalPrice: Int,
    val paymentMethod: String
) : Parcelable

@Parcelize
data class FormItem(
    val id: Int?,
    val type: String,
    val label: String,
    val options: List<OptionItem> = emptyList()
) : Parcelable

@Parcelize
data class OptionItem(
    val label: String,
    val value: String = label
) : Parcelable

@Parcelize
data class TimelineEvent(
    val iconRes: Int,
    val description: String,
    val date: String
): Parcelable
