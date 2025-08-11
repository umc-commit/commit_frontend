package com.example.commit.connection

import com.example.commit.data.model.CommissionFormResponse
import com.example.commit.data.model.ImageUploadResponse
import com.example.commit.data.model.CommissionRequestSubmit
import com.example.commit.data.model.CommissionRequestResponse
import okhttp3.MultipartBody
import com.example.commit.connection.dto.ApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Header
import retrofit2.http.DELETE
import com.example.commit.connection.dto.*
import retrofit2.http.HTTP


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

    @GET("/api/commissions/{commissionId}/forms")
    suspend fun getCommissionForm(
        @Path("commissionId") commissionId: String
    ): Response<CommissionFormResponse>

    @Multipart
    @POST("/api/commissions/request-images/upload")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @POST("/api/commissions/{commissionId}/requests")
    suspend fun submitCommissionRequest(
        @Path("commissionId") commissionId: String,
        @Body request: CommissionRequestSubmit
    ): Response<CommissionRequestResponse>

    // 채팅방 생성
    @POST("/api/chatrooms")
    fun createChatroom(
        @Body request: RetrofitClient.CreateChatroomRequest
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>

    // 채팅방 목록 조회
    @GET("/api/chatrooms/list")
    fun getChatroomList(): Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>

    // 커미션 상세보기 (postScreen)
    @GET("/api/commissions/{commissionId}")
    suspend fun getCommissionDetail(
        @Path("commissionId") commissionId: Int
    ): CommissionDetailResponse

    //신청함 목록
    @GET("/api/requests")
    suspend fun getRequestList(
    ):  ApiResponse<RequestListResponse>

    //신청함 상세 조회
    @GET("/api/requests/{requestId}")
    suspend fun getRequestDetail(
        @Path("requestId") requestId: Int
    ): ApiResponse<RequestDetailResponse>

    // 작가 팔로우
    @POST("/api/users/follows/{artistId}")
    fun followArtist(
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>

    // 작가 팔로우 취소
    @DELETE("/api/users/follows/{artistId}")
    fun unfollowArtist(
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>

    // 북마크 추가
    @POST("/api/commissions/{commissionId}/bookmarks")
    fun addBookmark(
        @Path("commissionId") commissionId: Long
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>

    // 북마크 목록
    @GET("/api/bookmarks")
    fun getBookmarks(
        @retrofit2.http.Query("sort") sort: String = "latest",
        @retrofit2.http.Query("page") page: Int = 1,
        @retrofit2.http.Query("limit") limit: Int = 12,
        @retrofit2.http.Query("excludeFullSlots") excludeFullSlots: Boolean = false
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>

    // 북마크 단일삭제
    @DELETE("/api/commissions/{commissionId}/bookmarks/{bookmarkId}")
    fun deleteBookmark(
        @Path("commissionId") commissionId: Long,
        @Path("bookmarkId") bookmarkId: Long
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>

    //북마크 선택삭제
    @HTTP(method = "DELETE", path = "/api/bookmarks", hasBody = true)
    fun deleteBookmarksBulk(
        @Body req: RetrofitClient.BookmarkBulkDeleteRequest
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkBulkDeleteSuccess>>

}
