package com.example.commit.ui.post.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter


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
    artistName: String,
    profileImageUrl: String,
    followerCount: Int,
    workCount: Int,
    rating: Float,
    recommendRate: Int,
    reviewCount: Int,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    onReviewListClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var followingState by remember { mutableStateOf(isFollowing) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 1. 프로필 이미지 표시 (Coil 사용)
            Image(
                painter = rememberAsyncImagePainter(model = profileImageUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = artistName,
                        style = CommitTypography.bodyMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "팔로워 $followerCount",
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
                        style = CommitTypography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                        color = Color(0xFF17D5C6)
                    )
                }
            }

            //2. 팔로우 상태에 따라 버튼 스타일 변경
            Box(
                modifier = Modifier
                    .background(
                        color = if (followingState) Color.White else Color.Black,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        onFollowClick()
                        followingState = !followingState
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (followingState) "팔로잉" else "팔로우",
                    style = CommitTypography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
                    color = if (followingState) Color.Black else Color.White
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
                    style = CommitTypography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Medium),
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
                        style = CommitTypography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
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
                        style = CommitTypography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
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
                if (!isExpanded) {
                    // 펼칠 때만 ReviewListScreen으로 이동
                    onReviewListClick()
                }
                isExpanded = !isExpanded
            }
        ) {
            if (reviewCount == 0) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .background(Color.White),
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
                ReviewItem(
                    rating = 5.0f,
                    title = "낚서 타임 커미션",
                    duration = "12시간",
                    content = "친절하게 응대해주셨습니다. 감사해요!",
                    writer = "워시",
                    date = "2일 전"
                )
            }
        }
    }
}


