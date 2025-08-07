package com.example.commit.connection

import com.example.commit.connection.dto.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import com.example.commit.connection.dto.CommissionDetailResponse
import com.example.commit.connection.dto.RequestListResponse

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

    // 커미션 상세보기 (postScreen)
    @GET("/api/commissions/{commissionId}")
    suspend fun getCommissionDetail(
        @Header("Authorization") token: String,
        @Path("commissionId") commissionId: Int
    ): CommissionDetailResponse

    //신청함 목록
    @GET("/api/requests")
    suspend fun getRequestList(
        @Header("Authorization") token: String
    ):  ApiResponse<RequestListResponse>
}
