package com.example.commit.data.model.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatItem(
    val profileImageRes: Int,  // drawable 리소스 ID (또는 URL)
    val name: String,          // 사용자 이름
    val message: String,       // 최근 메시지
    val time: String,          // 보낸 시간 ("방금 전", "2시간 전", 등)
    var isNew: Boolean,        // 새 메시지 여부 (알림 표시용)
    val title: String = ""     // 게시글 제목
) : Parcelable