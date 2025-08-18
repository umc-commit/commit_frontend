package com.example.commit.ui.post.components

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.commit.R
import com.example.commit.connection.dto.FollowHelper
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun ExpandableItem(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = CommitTypography.bodyMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(
                    id = if (expanded) R.drawable.ic_up_vector else R.drawable.ic_under_vector
                ),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun ArtistInfoSection(
    artistId: Int,                 // 추가: API 호출에 필요한 작가 ID
    isFollowingInitial: Boolean,
    artistName: String,
    followerCount: Int,
    workCount: Int,
    rating: Float,
    recommendRate: Int,
    reviewCount: Int,
    onReviewListClick: () -> Unit,
    profileImageUrl: String? = null,
    // 필요하면 팔로우 클릭 시 부모에 알림
    onFollowStateChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var isFollowing by remember { mutableStateOf(isFollowingInitial) }
    var isLoading by remember { mutableStateOf(false) }
    // 팔로워 수를 상태로 관리
    var followerCountState by remember { mutableStateOf(followerCount) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 아바타 공통 모디파이어
            val avatarModifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)

            // URL 있으면 로드, 없거나 실패/로딩 중이면 회색 원으로 대체
            if (!profileImageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = profileImageUrl,
                    contentDescription = "artist profile",
                    contentScale = ContentScale.Crop,
                    modifier = avatarModifier
                ) {
                    when (painter.state) {
                        is AsyncImagePainter.State.Success -> {
                            SubcomposeAsyncImageContent()
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Gray, CircleShape)
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = avatarModifier.background(Color.Gray, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = artistName,
                        style = CommitTypography.bodyMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "팔로워 $followerCountState",
                        style = CommitTypography.labelSmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = "총 작업수 ",
                        style = CommitTypography.labelSmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "${workCount}건",
                        style = CommitTypography.labelSmall.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        ),
                        color = Color(0xFF17D5C6)
                    )
                }
            }


            val followBg = if (!isFollowing) Color(0xFF4D4D4D) else Color.White
            val followBorder = if (!isFollowing) Color(0xFF4D4D4D) else Color.LightGray
            val followTextColor = if (!isFollowing) Color.White else Color.Black

            Box(
                modifier = Modifier
                    .border(1.dp, followBorder, RoundedCornerShape(20.dp))
                    .background(followBg, RoundedCornerShape(20.dp))
                    .clickable {
                        if (isLoading) return@clickable
                        val next = !isFollowing
                        // 낙관적 반영
                        isFollowing = next
                        isLoading = true

                        FollowHelper.toggleFollow(
                            context = context,
                            artistId = artistId,
                            follow = next
                        ) { success, nowFollowing, serverMessage ->
                            isLoading = false
                            isFollowing = nowFollowing

                            // 성공 시 팔로워 수 즉시 증감(최소 0 보장)
                            if (success) {
                                followerCountState = if (nowFollowing) {
                                    followerCountState + 1
                                } else {
                                    (followerCountState - 1).coerceAtLeast(0)
                                }
                            }
                            onFollowStateChanged(nowFollowing)

                            // 프로필 화면 동기화용 브로드캐스트
                            val intent = Intent(FollowHelper.ACTION_FOLLOW_CHANGED).apply {
                                putExtra(FollowHelper.EXTRA_ARTIST_ID, artistId)
                                putExtra(FollowHelper.EXTRA_IS_FOLLOWING, nowFollowing)
                                putExtra(FollowHelper.EXTRA_FOLLOWER_COUNT, followerCountState)
                            }
                            context.sendBroadcast(intent)

                            if (success) {
                                val msg = serverMessage ?: if (nowFollowing) "팔로우했습니다." else "팔로우를 취소했습니다."
                                Log.d("Follow", msg)
                            } else {
                                // 실패 시 안내
                                val msg = serverMessage ?: "네트워크 오류로 처리에 실패했습니다."
                                Log.d("Follow", msg)
                            }
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "팔로우",
                    style = CommitTypography.labelSmall.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    ),
                    color = followTextColor
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (reviewCount == 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "아직 남겨진 후기가 없어요.",
                    style = CommitTypography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    repeat(5) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = null,
                            tint = Color(0xFFD3D3D3),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "작가의 커미션 후기 ",
                    style = CommitTypography.labelSmall,
                    color = Color.Black
                )
                Text(
                    text = "${reviewCount}개",
                    style = CommitTypography.labelSmall.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    ),
                    color = Color(0xFF17D5C6)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%.1f", rating),
                        style = CommitTypography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star_yellow),
                                contentDescription = null,
                                modifier = Modifier.size(11.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(36.dp)
                        .background(Color(0xFFE0E0E0))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$recommendRate%",
                        style = CommitTypography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "추천해요",
                        style = CommitTypography.labelSmall,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ExpandableItem(
            title = "전체 후기 보기",
            expanded = isExpanded,
            onToggle = {
                if (!isExpanded) onReviewListClick()
                isExpanded = !isExpanded
            }
        ) {

        }
    }
}
