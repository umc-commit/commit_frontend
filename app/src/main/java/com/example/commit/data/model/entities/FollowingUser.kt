package com.example.commit.data.model.entities

data class FollowingUser(
    val profileImageResId: Int, // 프로필 이미지 리소스 ID (예: R.drawable.ic_pf_charac)
    val username: String,
    val followerCount: Int,
    var isFollowing: Boolean // 팔로잉 중인지 여부를 나타내는 필드 추가
)
