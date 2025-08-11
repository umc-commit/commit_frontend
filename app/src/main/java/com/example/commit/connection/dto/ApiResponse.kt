package com.example.commit.connection.dto

    data class ApiResponse<T>(
        val resultType: String,
        val error: ErrorResponse?,
        val success: T?
    )

    data class ErrorResponse(
        val reason: String?
    )

data class CommissionRequestError(
    val errorCode: String,
    val reason: String,
    val data: CommissionRequestErrorData?
)

data class CommissionRequestErrorData(
    val userId: String?,
    val commissionId: String?,
    val existingRequestId: String?
)

data class CommissionRequestErrorResponse(
    val resultType: String,
    val error: CommissionRequestError?,
    val success: Any?
)

