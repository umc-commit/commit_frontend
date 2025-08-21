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
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.activity.CommissionFormActivity
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.post.components.PostBottomBar
import com.example.commit.ui.post.components.PostDetailTabSection
import com.example.commit.ui.post.components.TabType
import com.example.commit.viewmodel.ArtistViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

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
    onChatClick: () -> Unit = {},
    // 상세 화면에서도 ViewModel로 위임할 북마크 토글 콜백
    onBookmarkToggle: (newState: Boolean) -> Unit
) {
    val context = LocalContext.current
    var selectedImageIndex by remember {
        mutableStateOf(currentIndex.coerceAtMost(images.lastIndex))
    }

    // 아티스트 탭 로딩용 (기존 로직 유지)
    val artistViewModel: ArtistViewModel = viewModel(key = "artist-$commissionId")
    val artistBlock by artistViewModel.artistBlock.collectAsState()
    val artistError by artistViewModel.artistError.collectAsState()

    // 북마크 토글 상태(로컬) + 외부와 동기화
    var isBookmarkedState by remember { mutableStateOf(isBookmarked) }
    LaunchedEffect(isBookmarked) { isBookmarkedState = isBookmarked }

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

            // 메인 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(233.dp)
            ) {
                if (images.isNotEmpty() && selectedImageIndex <= images.lastIndex) {
                    AsyncImage(
                        model = images[selectedImageIndex],
                        contentDescription = "썸네일 이미지",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                if (imageCount > 0) {
                                    selectedImageIndex = (selectedImageIndex + 1) % imageCount
                                }
                            }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray)
                    )
                }
            }

            // 이미지 인디케이터
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
                    val bookmarkPainter =
                        if (isBookmarkedState) painterResource(id = R.drawable.ic_select_bookmarket)
                        else painterResource(id = R.drawable.ic_unselect_bookmarket)

                    val bookmarkTint =
                        if (isBookmarkedState) Color.Unspecified else Color(0xFF222222)

                    Icon(
                        painter = bookmarkPainter,
                        contentDescription = if (isBookmarkedState) "북마크 해제" else "북마크",
                        tint = bookmarkTint,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val next = !isBookmarkedState
                                isBookmarkedState = next       // 낙관적 반영
                                onBookmarkToggle(next)         // VM로 위임(POST/DELETE 호출)
                            }
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

            // ▼ 탭 섹션: 탭 클릭 시 필요할 때만 API 호출
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
            isRecruiting = true,         // 필요 시 실제 값으로 교체
            remainingSlots = 11,         // 필요 시 실제 값으로 교체
            onApplyClick = {
                val intent = Intent(context, CommissionFormActivity::class.java)
                intent.putExtra("commissionId", commissionId.toString())
                Log.d("PostScreen", "신청하기 클릭 - commissionId: $commissionId")
                context.startActivity(intent)
            },
            onChatClick = onChatClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}
