package com.example.commit.util

import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.entities.ChatItem
import java.text.SimpleDateFormat
import java.util.*

fun chatMessageToChatItem(
    lastMessage: ChatMessage,
    profileImageRes: Int,
    name: String,
    isNew: Boolean,
    title: String = ""
): ChatItem {
    return ChatItem(
        profileImageRes = profileImageRes,
        name = name,
        message = lastMessage.content,
        time = formatRelativeTime(lastMessage.timestamp),
        isNew = isNew,
        title = title
    )
}

fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / 1000 / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        days < 7 -> "${days}일 전"
        else -> SimpleDateFormat("MM/dd", Locale.getDefault()).format(Date(timestamp))
    }
}
