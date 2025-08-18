package com.example.commit.connection.dto

data class SubmittedRequestFormsResponse(
    val requestId: Int,
    val status: String,
    val displayTime: String,
    val commission: CommissionSummary,
    val artist: ArtistInfo,
    val formResponses: List<FormResponseItem>,
    val requestContent: RequestContent?
) {
    data class CommissionSummary(
        val id: Int,
        val title: String
    )
    data class ArtistInfo(
        val id: Int,
        val nickname: String,
        val profileImageUrl: String?
    )
    data class FormResponseItem(
        val questionId: String,
        val questionLabel: String?,
        val answer: String?
    )
    data class RequestContent(
        val text: String?,
        val images: List<RequestImage>?
    )
    data class RequestImage(
        val id: Int,
        val imageUrl: String,
        val orderIndex: Int
    )
}
