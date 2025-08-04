package com.example.commit.connection

import com.google.gson.annotations.SerializedName

class RetrofitClient {

    // 회원가입 요청 DTO
    data class RequestSignUp(
        @SerializedName("token")
        val token: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("role")
        val role: String,
        @SerializedName("categories")
        val categories: List<Int>,
        @SerializedName("agreements")
        val agreements: List<Int>
    )

    // 회원가입 응답 DTO
    data class ResponseSignUp(
        @SerializedName("resultType")
        val resultType: String,
        @SerializedName("error")
        val error: ErrorData?,
        @SerializedName("success")
        val success: SuccessData?
    )

    data class ErrorData(
        @SerializedName("reason")
        val reason: String?
    )

    data class SuccessData(
        @SerializedName("message")
        val message: String,
        @SerializedName("token")
        val token: String,
        @SerializedName("user")
        val user: UserData
    )

    data class UserData(
        @SerializedName("userId")
        val userId: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("provider")
        val provider: String,
        @SerializedName("oauth_id")
        val oauthId: String,
        @SerializedName("createdAt")
        val createdAt: String
    )
}