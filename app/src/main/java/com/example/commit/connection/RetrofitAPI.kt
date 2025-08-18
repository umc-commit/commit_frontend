package com.example.commit.connection

import com.example.commit.data.model.CommissionFormResponse
import com.example.commit.data.model.ImageUploadResponse
import com.example.commit.data.model.CommissionRequestSubmit
import com.example.commit.data.model.CommissionRequestResponse
import com.example.commit.connection.dto.ApiResponse
import com.example.commit.connection.dto.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitAPI {

    // [회원가입] 토큰 불필요
    @POST("/api/users")
    fun signUp(
        @Body request: RetrofitClient.RequestSignUp
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.SignUpSuccessData>>

    // [내 프로필 조회] 토큰 자동 추가 (인터셉터)
    @GET("/api/users/me")
    fun getMyProfile(): Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileResponseData>>

    // [알림 목록 조회]
    @GET("/api/notifications")
    fun getNotifications(): Call<RetrofitClient.ApiResponse<RetrofitClient.NotificationResponseData>>

    // [커미션 리포트 조회]
    @GET("/api/commissions/reports")
    fun getCommissionReport(): Call<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>>

    // [홈 화면 조회]
    @GET("/api/home")
    fun getHomeData(): Call<RetrofitClient.ApiResponse<RetrofitClient.HomeResponseData>>

    // [작가 프로필 조회]
    @GET("/api/artists/{artistId}")
    fun getAuthorProfile(
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.AuthorProfileResponse>

    // [커미션 신청폼 스키마 조회] (작성용) - suspend + Response
    @GET("/api/commissions/{commissionId}/forms")
    suspend fun getCommissionForm(
        @Path("commissionId") commissionId: String
    ): Response<CommissionFormResponse>

    // [신청 이미지 업로드]
    @Multipart
    @POST("/api/commissions/request-images/upload")
    suspend fun uploadRequestImage(
        @Part image: MultipartBody.Part
    ): Response<ImageUploadResponse>

    // [커미션 신청 제출]
    @POST("/api/commissions/{commissionId}/requests")
    suspend fun submitCommissionRequest(
        @Path("commissionId") commissionId: String,
        @Body request: CommissionRequestSubmit
    ): Response<CommissionRequestResponse>

    // [채팅방 생성]
    @POST("/api/chatrooms")
    fun createChatroom(
        @Body request: RetrofitClient.CreateChatroomRequest
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>

    // [채팅방 목록 조회]
    @GET("/api/chatrooms/list")
    fun getChatroomList(): Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>

    // [채팅방 메시지 조회]
    @GET("/api/chatrooms/{chatroomId}/messages")
    fun getChatroomMessages(
        @Path("chatroomId") chatroomId: Int,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>

    // [커미션 상세보기(PostScreen)]
    @GET("/api/commissions/{commissionId}")
    fun getCommissionDetail(
        @Path("commissionId") commissionId: Int
    ): Call<CommissionDetailResponse>

    // [제출된 신청서 보기] (동일 path이지만 다른 DTO 사용)
    @GET("/api/commissions/{commissionId}/forms")
    fun getSubmittedCommissionForm(
        @Path("commissionId") commissionId: Int
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>

    // [신청함 목록]
    @GET("/api/requests")
    suspend fun getRequestList(
        @Query("page") page: Int,
        @Query("limit") limit: Int? = null
    ): ApiResponse<RequestListResponse>

    // [신청함 상세]
    @GET("/api/requests/{requestId}")
    suspend fun getRequestDetail(
        @Path("requestId") requestId: Int
    ): ApiResponse<RequestDetailResponse>

    // [검색 결과 조회] — keyword/categoryId nullable (합의 반영)
    @GET("/api/search")
    suspend fun getSearchResults(
        @Query("keyword") keyword: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("sort") sort: String? = "latest",
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null,
        @Query("deadline") deadline: String? = "all",
        @Query("followingOnly") followingOnly: Boolean? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12
    ): ApiResponse<SearchSuccess>

    // [팔로우]
    @POST("/api/users/follows/{artistId}")
    fun followArtist(
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>

    // [팔로우 취소]
    @DELETE("/api/users/follows/{artistId}")
    fun unfollowArtist(
        @Path("artistId") artistId: Int
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>

    // [북마크 추가]
    @POST("/api/commissions/{commissionId}/bookmarks")
    fun addBookmark(
        @Path("commissionId") commissionId: Long
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>

    // [북마크 목록]
    @GET("/api/bookmarks")
    fun getBookmarks(
        @Query("sort") sort: String = "latest",
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12,
        @Query("excludeFullSlots") excludeFullSlots: Boolean = false
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>

    // [북마크 단일 삭제]
    @DELETE("/api/commissions/{commissionId}/bookmarks/{bookmarkId}")
    fun deleteBookmark(
        @Path("commissionId") commissionId: Long,
        @Path("bookmarkId") bookmarkId: Long
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>

    // [북마크 선택 삭제]
    @HTTP(method = "DELETE", path = "/api/bookmarks", hasBody = true)
    fun deleteBookmarksBulk(
        @Body req: RetrofitClient.BookmarkBulkDeleteRequest
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkBulkDeleteSuccess>>

    // [팔로잉 작가 커미션 조회]
    @GET("/api/home/following")
    fun getFollowing(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FollowingResponseData>>

     //커미션 작가 정보 조회
    @GET("/api/commissions/{commissionId}/artist")
    fun getCommissionArtist(
        @Path("commissionId") commissionId: Int,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Call<ApiResponse<CommissionArtistResponse>>

    // 프로필 수정
    @PATCH("/api/users/me")
    fun updateMyProfile(
        @Body body: RetrofitClient.ProfileUpdateRequest
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileUpdateResponse>>


    // 닉네임 중복 확인
    @GET("/api/users/check-nickname")
    fun checkNickname(
        @Query("nickname") nickname: String
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.NicknameCheckResponse>>

    // 사용자가 팔로우한 작가 조회
    @GET("/api/users/follows")
    fun getFollowedArtists(
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FollowedArtistsSuccess>>

    // FCM 토큰 등록
    @POST("/api/notifications/fcm/token")
    fun registerFcmToken(
        @Body body: RetrofitClient.FcmTokenRegisterRequest
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>

    // FCM 토큰 삭제
    @DELETE("/api/notifications/fcm/token")
    fun deleteFcmToken(
    ): Call<RetrofitClient.ApiResponse<RetrofitClient.SimpleMessage>>

    //제출된 신청폼 조회
    @GET("/api/requests/{requestId}/forms")
    fun getSubmittedRequestForms(
        @Path("requestId") requestId: Int
    ): Call<RetrofitClient.ApiResponse<SubmittedRequestFormsResponse>>
}
