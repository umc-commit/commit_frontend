package com.example.commit.ui.post

import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.activity.CommissionFormActivity
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.post.components.PostBottomBar
import com.example.commit.ui.post.components.PostDetailTabSection
import com.example.commit.ui.post.components.TabType
import com.example.commit.viewmodel.ArtistViewModel

@Composable
fun PostScreen(
    title: String,
    tags: List<String>,
    minPrice: Int,
    summary: String,
    content: String,
    images: List<String>,
    isBookmarked: Boolean,
    imageCount: Int = images.size,
    currentIndex: Int = 0,
    commissionId: Int = -1,
    onReviewListClick: () -> Unit,
    onChatClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedImageIndex by remember { mutableStateOf(currentIndex) }

    val artistViewModel: ArtistViewModel = viewModel()
    val artistBlock by artistViewModel.artistBlock.collectAsState()
    val artistError by artistViewModel.artistError.collectAsState()

    // 북마크 토글 상태
    var isBookmarkedState by remember { mutableStateOf(isBookmarked) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // 상단바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .background(Color.White)
            ) {
                val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
                Image(
                    painter = painterResource(id = R.drawable.ic_left_vector),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .offset(y = 12.dp)
                        .size(24.dp)
                        .clickable { backDispatcher?.onBackPressed() }
                )
                Text(
                    text = title,
                    style = CommitTypography.headlineSmall,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(233.dp)
            ) {
                if (images.isNotEmpty()) {
                    AsyncImage(
                        model = images[selectedImageIndex],
                        contentDescription = "썸네일 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { selectedImageIndex = (selectedImageIndex + 1) % imageCount }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
            }

            // 인디케이터
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(imageCount) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(8.dp)
                            .background(
                                color = if (index == selectedImageIndex) Color.Black else Color.LightGray,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            // 본문 헤더
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = CommitTypography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    // ▼ 북마크 아이콘 (토글)
                    val bookmarkPainter = if (isBookmarkedState)
                        painterResource(id = R.drawable.ic_select_bookmarket)
                    else
                        painterResource(id = R.drawable.ic_unselect_bookmarket)

                    val bookmarkTint = if (isBookmarkedState) {
                        // 선택 시 현재 UI 유지 (아이콘 고유 색 사용)
                        Color.Unspecified
                    } else {
                        // 미선택 시 #222222
                        Color(0xFF222222)
                    }

                    Icon(
                        painter = bookmarkPainter,
                        contentDescription = if (isBookmarkedState) "북마크 해제" else "북마크",
                        tint = bookmarkTint,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { isBookmarkedState = !isBookmarkedState }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 태그
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEachIndexed { index, tag ->
                        val isCategory = index == 0
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .wrapContentWidth()
                                .background(
                                    color = if (isCategory) Color(0xFFE6FFFB) else Color(0xFFF2F2F2),
                                    shape = RoundedCornerShape(50)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isCategory) Color(0xFF17D5C6) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                style = CommitTypography.labelSmall,
                                color = if (isCategory) Color(0xFF17D5C6) else Color(0xFFB0B0B0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = String.format("%,dP~", minPrice),
                    style = CommitTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = summary,
                    style = CommitTypography.bodyMedium,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // ▼ 탭 섹션: 여기서 탭 클릭 시 곧바로 API 호출
            PostDetailTabSection(
                onTabSelected = { tab ->
                    if (tab == TabType.ARTIST && artistBlock == null && commissionId > 0) {
                        artistViewModel.loadArtist(
                            context = context,
                            commissionId = commissionId,
                            page = 1,
                            limit = 10
                        )
                    }
                },
                onReviewListClick = onReviewListClick,
                artistBlock = artistBlock,
                artistError = artistError,
                detailContent = content
            )
        }

        // 하단 바
        PostBottomBar(
            isRecruiting = true,
            remainingSlots = 11,
            onApplyClick = {
                val intent = Intent(context, CommissionFormActivity::class.java)
                intent.putExtra("commissionId", commissionId.toString())
                Log.d("PostScreen", "신청하기 버튼 클릭 - commissionId: $commissionId")
                context.startActivity(intent)
            },
            onChatClick = onChatClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}
