package com.example.commit.data.model.entities

data class AlarmItem(
    val id: Long,
    val type: String,
    val title: String,
    val content: String,
    val time: String,  // 서버 created_at 가공해서 사용
    var isRead: Boolean,
    val requestId: Long? = null,
    val amount: Int? = null,
    val creatorName: String? = null,
    val creatorId: Long? = null
)

