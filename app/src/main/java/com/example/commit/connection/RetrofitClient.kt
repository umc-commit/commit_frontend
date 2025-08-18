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
        val badges: List<UserBadge>,
        @SerializedName("reviews")
        val reviews: List<UserReview>
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

    data class UserReview(
        @SerializedName("id")
        val id: String,
        @SerializedName("requestId")
        val requestId: String,
        @SerializedName("rate")
        val rate: Int,
        @SerializedName("content")
        val content: String,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("updatedAt")
        val updatedAt: String,
        @SerializedName("reviewThumbnail")
        val reviewThumbnail: String?
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
        @SerializedName("nickname") val nickname: String,
        @SerializedName("description") val description: String,
        @SerializedName("profileImage") val profileImage: String?,
        @SerializedName("slot") val slot: Int,
        @SerializedName("reviews") val reviews: List<AuthorReviewItem>,
        @SerializedName("commissions") val commissions: List<AuthorCommissionItem>,
        @SerializedName("badges") val badges: List<AuthorBadgeItem>,
        @SerializedName("followerCount") val followerCount: Int
    )

    data class AuthorReviewItem(
        @SerializedName("id") val id: String,
        @SerializedName("rate") val rate: Int,
        @SerializedName("content") val content: String,
        @SerializedName("createdAt") val createdAt: String,
        @SerializedName("commissionTitle") val commissionTitle: String,
        @SerializedName("workingTime") val workingTime: String?,
        @SerializedName("review_thumbnail") val reviewThumbnail: String?,
        @SerializedName("writer") val writer: Writer
    )

    data class Writer(
        @SerializedName("nickname") val nickname: String
    )

    data class AuthorCommissionItem(
        @SerializedName("id") val id: String,
        @SerializedName("title") val title: String,
        @SerializedName("summary") val summary: String,
        @SerializedName("minPrice") val minPrice: Int,
        @SerializedName("category") val category: String,
        @SerializedName("tags") val tags: List<String>,
        @SerializedName("commission_img") val commission_img: String?,
        @SerializedName("bookmark") val isBookmarked: Boolean = false
    )

    data class AuthorBadgeItem(
        @SerializedName("id") val id: String,
        @SerializedName("earnedAt") val earnedAt: String,
        @SerializedName("badge") val badge: List<BadgeInfo>
    )

    data class BadgeInfo(
        @SerializedName("id") val id: String,
        @SerializedName("type") val type: String,
        @SerializedName("threshold") val threshold: Int,
        @SerializedName("name") val name: String,
        @SerializedName("badgeImage") val badgeImage: String
    )

    // 채팅방 생성 요청 DTO
    data class CreateChatroomRequest(
        @SerializedName("artistId")
        val artistId: Int,
        @SerializedName("commissionId")
        val commissionId: Int
    )

    // 채팅방 생성 응답 DTO
    data class CreateChatroomResponse(
        @SerializedName("id")
        val id: String,
        @SerializedName("commissionId")
        val commissionId: String,
        @SerializedName("userId")
        val userId: String,
        @SerializedName("artistId")
        val artistId: String,
        @SerializedName("hiddenArtist")
        val hiddenArtist: Boolean,
        @SerializedName("hiddenUser")
        val hiddenUser: Boolean,
        @SerializedName("createdAt")
        val createdAt: String
    )

    // 채팅방 목록 응답 DTO - success 필드가 직접 배열임
    data class ChatroomListResponse(
        val chatrooms: List<ChatroomItem>
    )

    // 채팅방 목록 DTO
    data class ChatroomItem(
        @SerializedName("chatroom_id")
        val chatroomId: String,
        @SerializedName("artist_id")
        val artistId: String,
        @SerializedName("artist_nickname")
        val artistNickname: String,
        @SerializedName("artist_profile_image")
        val artistProfileImage: String?,
        @SerializedName("commission_id")
        val commissionId: String,
        @SerializedName("commission_title")
        val commissionTitle: String,
        @SerializedName("last_message")
        val lastMessage: String?,
        @SerializedName("last_message_time")
        val lastMessageTime: String?,
        @SerializedName("has_unread")
        val hasUnread: Int
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

    // 채팅 메시지 DTO (실제 API 스펙에 맞춤)
    data class ChatMessage(
        @SerializedName("messageId")
        val messageId: Int,
        @SerializedName("sender_id")
        val senderId: Int,
        @SerializedName("content")
        val content: String,
        @SerializedName("image_id")
        val imageId: Int? = null,
        @SerializedName("created_at")
        val createdAt: String
    )

    // 제출된 신청서 데이터 DTO
    data class SubmittedFormData(
        @SerializedName("commission")
        val commission: com.example.commit.data.model.CommissionInfo,
        @SerializedName("formSchema")
        val formSchema: Map<String, Any>,
        @SerializedName("formAnswer")
        val formAnswer: Map<String, Any>
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
        @SerializedName("minPrice") val minPrice: Int?,
        @SerializedName("category") val category: BookmarkCategory,
        @SerializedName("tags") val tags: List<BookmarkTag>,
        @SerializedName("thumbnailImageUrl") val thumbnailImageUrl: String?,
        @SerializedName("remainingSlots") val remainingSlots: Int,
        @SerializedName("artist") val artist: CommissionArtist // 기존 타입 재사용(id, nickname, profileImageUrl)
    )

    data class BookmarkCategory(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String
    )

    data class BookmarkTag(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String
    )

    // 북마크 선택삭제
    data class BookmarkBulkDeleteRequest(
        @SerializedName("bookmarkIds") val bookmarkIds: List<Long>
    )

    data class BookmarkBulkDeleteSuccess(
        @SerializedName("bookmarkIds") val bookmarkIds: List<Long>?, // 서버가 주는 배열
        @SerializedName("message") val message: String?
    )

    // 팔로잉 작가 커미션 조회
    data class FollowingResponseData(
        @SerializedName("items") val items: List<FollowingPostItem>,
        @SerializedName("pagination") val pagination: FollowingPagination
    )

    data class FollowingPagination(
        @SerializedName("page") val page: Int,
        @SerializedName("limit") val limit: Int,
        @SerializedName("totalCount") val totalCount: Int,
        @SerializedName("totalPages") val totalPages: Int
    )

    data class FollowingPostItem(
        @SerializedName("id") val id: Long,
        @SerializedName("title") val title: String,
        @SerializedName("summary") val summary: String,
        @SerializedName("images") val images: List<FollowingImage>,
        @SerializedName("timeAgo") val timeAgo: String,
        @SerializedName("createdAt") val createdAt: String,
        @SerializedName("isBookmarked") val isBookmarked: Boolean,
        @SerializedName("artist") val artist: FollowingArtist
    )

    data class FollowingImage(
        @SerializedName("id") val id: Long,
        @SerializedName("imageUrl") val imageUrl: String,
        @SerializedName("orderIndex") val orderIndex: Int
    )

    data class FollowingArtist(
        @SerializedName("id") val id: Long,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("profileImageUrl") val profileImageUrl: String?,
        @SerializedName("followCount") val followCount: Int
    )

    // 프로필 수정
    data class ProfileUpdateRequest(
        val nickname: String? = null,
        val profileImage: String? = null,
        val description: String? = null
    )

    data class ProfileUpdateResponse(
        @SerializedName("message") val message: String,
        @SerializedName("user") val user: RetrofitClient.UserProfile
    )

    // 닉네임 중복 확인
    data class NicknameCheckResponse(
        @SerializedName("message") val message: String,
        @SerializedName("nickname") val nickname: String
    )

    // 사용자가 팔로우한 작가 조회
    data class FollowedArtistsSuccess(
        @SerializedName("message") val message: String,
        @SerializedName("artistList") val artistList: List<FollowedArtistContainer>
    )
    data class FollowedArtistContainer(
        @SerializedName("artist") val artist: FollowedArtist
    )
    data class FollowedArtist(
        @SerializedName("id") val id: String,
        @SerializedName("nickname") val nickname: String,
        @SerializedName("profileImage") val profileImage: String?,
        @SerializedName("followerCount") val followerCount: Int
    )

    // FCM 토큰 등록 - 요청/응답 DTO
    data class FcmTokenRegisterRequest(
        @SerializedName("fcm_token") val fcmToken: String
    )

    data class FcmTokenRegisterSuccess(
        @SerializedName("id") val id: String,
        @SerializedName("user_id") val userId: String,
        @SerializedName("fcm_token") val fcmToken: String,
        @SerializedName("is_active") val isActive: Boolean,
        @SerializedName("created_at") val createdAt: String,
        @SerializedName("updated_at") val updatedAt: String
    )

    // FCM 토큰 삭제 공용 메시지 응답 DTO (success: { message: "..." })
    data class SimpleMessage(
        @SerializedName("message") val message: String
    )



}