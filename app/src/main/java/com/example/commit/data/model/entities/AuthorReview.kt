package com.example.commit.data.model.entities

data class AuthorReview(
    val rating: Double,
    val commissionName: String,
    val content: String,
    val reviewer: String,
    val time: String,
    val workPeriod: String
)
