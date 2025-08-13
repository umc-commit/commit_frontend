package com.example.commit.ui.post.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.connection.dto.CommissionArtistResponse
import com.example.commit.ui.Theme.CommitTypography

enum class TabType { DETAIL, ARTIST }

@Composable
fun PostDetailTabSection(
    onTabSelected: (TabType) -> Unit,
    onReviewListClick: () -> Unit,
    artistBlock: CommissionArtistResponse?,
    artistError: String?,
    detailContent: String,
) {
    var selectedTab by remember { mutableStateOf(TabType.DETAIL) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // 탭 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            TabType.values().forEach { tab ->
                Column(
                    modifier = Modifier
                        .clickable {
                            selectedTab = tab
                            onTabSelected(tab)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (tab == TabType.DETAIL) "상세글" else "작가정보",
                        style = CommitTypography.bodyMedium.copy(
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selectedTab == tab) Color.Black else Color(0xFFB0B0B0)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(2.dp)
                            .background(if (selectedTab == tab) Color(0xFF17D5C6) else Color.Transparent)
                    )
                }
            }
        }

        // 탭 콘텐츠
        when (selectedTab) {
            TabType.DETAIL -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(
                        text = if (detailContent.isBlank()) " " else detailContent,
                        style = CommitTypography.bodyMedium,
                        fontSize = 12.sp,
                        color = Color(0xFF333333)
                    )
                }
            }

            TabType.ARTIST -> {
                when {
                    artistError != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "작가 정보를 불러오지 못했습니다.\n$artistError",
                                style = CommitTypography.bodyMedium,
                                color = Color(0xFFDD3333)
                            )
                        }
                    }
                    artistBlock == null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "작가 정보를 불러오는 중...",
                                style = CommitTypography.bodyMedium,
                                color = Color(0xFF666666)
                            )
                        }
                    }
                    else -> {
                        ArtistInfoSection(
                            artistName = artistBlock.artist.nickname,
                            followerCount = artistBlock.artist.follower,
                            workCount = artistBlock.artist.completedworks,
                            rating = artistBlock.reviewStatistics.averageRate.toFloat(),
                            recommendRate = artistBlock.reviewStatistics.recommendationRate,
                            reviewCount = artistBlock.reviewStatistics.totalReviews,
                            onFollowClick = { /* TODO: 팔로우 토글 */ },
                            onReviewListClick = onReviewListClick
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPostDetailTabSection() {
    PostDetailTabSection(
        onTabSelected = {},
        onReviewListClick = {},
        artistBlock = null,
        artistError = null,
        detailContent = "프리뷰용 상세 내용입니다."   // ★ 추가
    )
}
