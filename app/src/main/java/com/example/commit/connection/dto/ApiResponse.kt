package com.example.commit.connection.dto

    data class ApiResponse<T>(
        val resultType: String,
        val error: ErrorResponse?,
        val success: T?
    )

    data class ErrorResponse(
        val reason: String?
    )

