package com.example.commit.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitAPI {
    // 회원가입 (토큰 불필요)
    @POST("/api/users")
    fun signUp(
        @Body request: RetrofitClient.RequestSignUp
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.SignUpSuccessData>>

    // 내 프로필 조회 (토큰 자동 추가)
    @GET("/api/users/me")
    fun getMyProfile(): Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>

    // 알림 목록 조회 (토큰 자동 추가)
    @GET("/api/notifications")
    fun getNotifications(): Call<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>>

    // 커미션 리포트 조회 (토큰 자동 추가)
    @GET("/api/commissions/reports")
    fun getCommissionReport(): Call<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>>

    // 홈 화면 조회 (토큰 자동 추가)
    @GET("/api/home")
    fun getHomeData(): Call<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>>

    // 작가 프로필 조회 (토큰 자동 추가)
    @GET("/api/artists/{artistId}")
    fun getAuthorProfile(
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.AuthorProfileResponse>
}
