package com.example.commit.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RetrofitAPI {
    // 회원가입
    @POST("/api/users")
    fun signUp(
        @Body request: RetrofitClient.RequestSignUp
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.SignUpSuccessData>>

    //내 프로필 조회
    @GET("/api/users/me")
    fun getMyProfile(
        @Header("Authorization") token: String
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>

    //알림 목록 조회
    @GET("/api/notifications")
    fun getNotifications(
        @Header("Authorization") token: String
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>>

    //커미션 리포트 조회
    @GET("/api/commissions/reports")
    fun getCommissionReport(
        @Header("Authorization") token: String
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>>

    // 홈 화면 조회
    @GET("/api/home")
    fun getHomeData(
        @Header("Authorization") token: String
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>>

    //작가 프로필 조회
    @GET("/api/artists/{artistId}")
    fun getAuthorProfile(
        @Header("Authorization") token: String,
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.AuthorProfileResponse>
}
