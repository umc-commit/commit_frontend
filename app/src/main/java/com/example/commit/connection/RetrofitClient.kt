package com.example.commit.connection

import com.google.gson.annotations.SerializedName

class RetrofitClient {
    //공통 API 응답 구조
    data class ApiResponse<T>(
        @SerializedName("resultType")
        val resultType: String,
        @SerializedName("error")
        val error: ErrorData?,
        @SerializedName("success")
        val success: T?
    )

    data class ErrorData(
        @SerializedName("errorCode")
        val errorCode: String?,
        @SerializedName("reason")
        val reason: String?,
        @SerializedName("data")
        val data: Any?
    )

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
    data class SignUpSuccessData(
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

    //내 프로필 조회 api
    data class ProfileResponseData(
        @SerializedName("message")
        val message: String,
        @SerializedName("user")
        val user: UserProfile
    )

    data class UserProfile(
        @SerializedName("userId")
        val userId: String?,
        @SerializedName("artistId")
        val artistId: String?,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("profileImage")
        val profileImage: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("badges")
        val badges: List<UserBadge>
    )

    data class UserBadge(
        @SerializedName("id")
        val id: String,
        @SerializedName("earnedAt")
        val earnedAt: String,
        @SerializedName("badge")
        val badge: List<BadgeDetail>
    )

    data class BadgeDetail(
        @SerializedName("id")
        val id: String,
        @SerializedName("type")
        val type: String,
        @SerializedName("threshold")
        val threshold: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("badgeImage")
        val badgeImage: String
    )

    data class NotificationResponseData(
        @SerializedName("items")
        val items: List<NotificationItem>,
        @SerializedName("pagination")
        val pagination: Pagination
    )

    data class NotificationItem(
        @SerializedName("id")
        val id: String,
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("is_read")
        val isRead: Boolean,
        @SerializedName("type")
        val type: String,
        @SerializedName("related_data")
        val relatedData: Map<String, Any> // 필요하면 세부 모델로 변경
    )

    data class RelatedData(
        val amount: Int? = null,
        val requestId: Long? = null,
        val creatorName: String? = null,
        val commissionTitle: String? = null,
        val artistId: Long? = null,
        val nickname: String? = null,
        val commissionId: Long? = null
    )

    data class Pagination(
        @SerializedName("page")
        val page: Int,
        @SerializedName("limit")
        val limit: Int,
        @SerializedName("total")
        val total: Int,
        @SerializedName("totalPages")
        val totalPages: Int
    )

    // 커미션 리포트 응답 DTO
    data class ReportResponseData(
        @SerializedName("reportInfo")
        val reportInfo: ReportInfo,
        @SerializedName("characterImage")
        val characterImage: String,
        @SerializedName("quote")
        val quote: ReportQuote,
        @SerializedName("condition")
        val condition: String,
        @SerializedName("statistics")
        val statistics: ReportStatistics
    )

    data class ReportInfo(
        @SerializedName("userNickname")
        val userNickname: String,
        @SerializedName("month")
        val month: Int
    )

    data class ReportQuote(
        @SerializedName("title")
        val title: String,
        @SerializedName("description")
        val description: String
    )

    data class ReportStatistics(
        @SerializedName("mainCategory")
        val mainCategory: MainCategory,
        @SerializedName("favoriteArtist")
        val favoriteArtist: FavoriteArtist,
        @SerializedName("pointsUsed")
        val pointsUsed: Int,
        @SerializedName("reviewRate")
        val reviewRate: Double
    )

    data class MainCategory(
        @SerializedName("name")
        val name: String,
        @SerializedName("count")
        val count: Int
    )

    data class FavoriteArtist(
        @SerializedName("id")
        val id: String,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("profileImage")
        val profileImage: String
    )

    // 홈 화면 응답 DTO
    data class HomeResponseData(
        @SerializedName("section1") val section1: List<HomeCommissionItem>,
        @SerializedName("section2") val section2: List<HomeCommissionItem>,
        @SerializedName("section3") val section3: List<HomeCommissionItem>,
        @SerializedName("section4") val section4: List<HomeCommissionItem>,
        @SerializedName("newReview") val newReview: List<HomeReviewItem>,
        @SerializedName("newArtist") val newArtist: List<HomeAuthorItem>
    )

    data class HomeCommissionItem(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("category") val category: String,
        @SerializedName("tags") val tags: List<String>,
        @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String?,
        @SerializedName("isBookmarked") val isBookmarked: Boolean,
        @SerializedName("artist") val artist: CommissionArtist
    )

    data class CommissionArtist(
        @SerializedName("id") val id: Int,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("profileImageUrl") val profileImageUrl: String?
    )

    data class HomeReviewItem(
        @SerializedName("id") val id: Int,
        @SerializedName("rate") val rate: Int,
        @SerializedName("content") val content: String,
        @SerializedName("duration") val duration: String,
        @SerializedName("reviewImageUrl") val reviewImageUrl: String?,
        @SerializedName("user") val user: ReviewUser,
        @SerializedName("commission") val commission: ReviewCommission
    )

    data class ReviewUser(
        @SerializedName("id") val id: Int,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("profileImageUrl") val profileImageUrl: String?
    )

    data class ReviewCommission(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String
    )

    data class HomeAuthorItem(
        @SerializedName("id") val id: Int,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("profileImageUrl") val profileImageUrl: String?
    )

    // 작가 프로필 응답 DTO
    data class AuthorProfileResponse(
        @SerializedName("resultType") val resultType: String,
        @SerializedName("error") val error: ErrorData?,
        @SerializedName("success") val success: AuthorProfileData?
    )

    data class AuthorProfileData(
        val nickname: String,
        val description: String,
        val profileImage: String?,
        val slot: Int,
        val reviews: List<AuthorReviewItem>,
        val commissions: List<AuthorCommissionItem>,
        val badges: List<AuthorBadgeItem>
    )

    data class AuthorReviewItem(
        val id: String,
        val rate: Double,
        val content: String,
        val createdAt: String,
        val commissionTitle: String,
        val workingTime: String?,
        val writer: Writer
    )

    data class Writer(
        val nickname: String
    )

    data class AuthorCommissionItem(
        val id: String,
        val title: String,
        val summary: String,
        val minPrice: Int,
        val category: String,
        val tags: List<String>,
        @SerializedName("commission_img") val commission_img: String?
    )

    data class AuthorBadgeItem(
        val id: String,
        val earnedAt: String,
        val badge: List<BadgeInfo>
    )

    data class BadgeInfo(
        val id: String,
        val type: String,
        val threshold: Int,
        val name: String,
        val badgeImage: String
    )

    // 채팅방 생성 요청 DTO
    data class CreateChatroomRequest(
        @SerializedName("consumerId")
        val consumerId: Int,
        @SerializedName("artistId")
        val artistId: Int,
        @SerializedName("requestId")
        val requestId: Int
    )

    // 채팅방 생성 응답 DTO
    data class CreateChatroomResponse(
        @SerializedName("id")
        val id: Int,
        @SerializedName("consumerId")
        val consumerId: Int,
        @SerializedName("artistId")
        val artistId: Int,
        @SerializedName("requestId")
        val requestId: Int
    )

    // 채팅방 목록 응답 DTO - success 필드가 직접 배열임
    data class ChatroomListResponse(
        val chatrooms: List<ChatroomItem>
    )

    data class ChatroomItem(
        @SerializedName("id")
        val id: Int,
        @SerializedName("consumerId")
        val consumerId: Int,
        @SerializedName("artistId")
        val artistId: Int,
        @SerializedName("requestId")
        val requestId: Int,
        @SerializedName("lastMessage")
        val lastMessage: String?,
        @SerializedName("lastMessageTime")
        val lastMessageTime: String?,
        @SerializedName("unreadCount")
        val unreadCount: Int,
        @SerializedName("artist")
        val artist: ChatroomArtist?,
        @SerializedName("consumer")
        val consumer: ChatroomConsumer?,
        @SerializedName("request")
        val request: ChatroomRequest?
    )

    data class ChatroomArtist(
        @SerializedName("id")
        val id: Int,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("profileImage")
        val profileImage: String?
    )

    data class ChatroomConsumer(
        @SerializedName("id")
        val id: Int,
        @SerializedName("nickname")
        val nickname: String,
        @SerializedName("profileImage")
        val profileImage: String?
    )

    data class ChatroomRequest(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String
    )

    data class FollowSuccess(
        @SerializedName("message") val message: String,
        @SerializedName("artistId") val artistId: String
    )

    // 북마크 추가 응답
    data class BookmarkAddSuccess(
        @SerializedName("bookmarkId") val bookmarkId: Long,
        @SerializedName("commissionId") val commissionId: Long,
        @SerializedName("message") val message: String
    )

    // 북마크 삭제 응답
    data class BookmarkDeleteSuccess(
        @SerializedName("bookmarkId") val bookmarkId: Long,
        @SerializedName("commissionId") val commissionId: Long,
        @SerializedName("message") val message: String
    )

    // 북마크 목록 조회
    data class BookmarkListSuccess(
        @SerializedName("items") val items: List<BookmarkCommissionItem>,
        @SerializedName("pagination") val pagination: BookmarkPagination
    )

    data class BookmarkPagination(
        @SerializedName("page") val page: Int,
        @SerializedName("limit") val limit: Int,
        @SerializedName("totalCount") val totalCount: Int,
        @SerializedName("totalPages") val totalPages: Int
    )

    data class BookmarkCommissionItem(
        @SerializedName("id") val id: Int, // commissionId
        @SerializedName("bookmarkId") val bookmarkId: Long?, // 선택삭제용(서버가 주면 채워짐)
        @SerializedName("title") val title: String,
        @SerializedName("minPrice") val minPrice: Int,
        @SerializedName("category") val category: BookmarkCategory,
        @SerializedName("tags") val tags: List<BookmarkTag>,
        @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String?,
        @SerializedName("remainingSlots") val remainingSlots: Int,
        @SerializedName("artist") val artist: CommissionArtist // 기존 타입 재사용(id, nickname, profileImageUrl)
    )

    data class BookmarkCategory(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
    )

    data class BookmarkTag(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
    )

    // 북마크 선택삭제
    data class BookmarkBulkDeleteRequest(
        @SerializedName("bookmarkIds") val bookmarkIds: List<Long>
    )

    data class BookmarkBulkDeleteSuccess(
        @SerializedName("deletedIds") val deletedIds: List<Long>,
        @SerializedName("notFoundIds") val notFoundIds: List<Long>,
        @SerializedName("message") val message: String
    )


}