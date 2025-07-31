package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.data.model.Artist
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun RequestCard(item: RequestItem, onClick: () -> Unit) {
    val isPending = item.status == "PENDING"
    val isInProgress = item.status == "IN_PROGRESS" || item.status == "ACCEPTED"
    val isCancel = item.status == "CANCEL"
    val isReject = item.status == "REJECT"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 2.dp,
            backgroundColor = Color.White,
            modifier = Modifier
                .width(344.dp)
                .padding(vertical = 8.dp)
                .clickable { onClick() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 상단 상태 표시
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val statusText = when (item.status) {
                        "PENDING" -> "수락 대기"
                        "IN_PROGRESS", "ACCEPTED" -> "진행 중"
                        "CANCEL" -> "신청 취소"
                        "REJECT" -> "신청 거절"
                        else -> "작업 완료"
                    }

                    val statusColor = when {
                        isInProgress -> Color(0xFF17D5C6)
                        isCancel -> Color(0xFFFF4D4D)
                        isReject -> Color(0xFFFF4D4D)
                        else -> Color.Black
                    }
                    Text(
                        text = statusText,
                        style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = statusColor
                    )

                    Icon(
                        painter = painterResource(id = R.drawable.vector),
                        contentDescription = "화살표",
                        tint = Color(0xFF4D4D4D),
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 썸네일 + 정보
                Row {
                    AsyncImage(
                        model = item.thumbnailImage,
                        contentDescription = "Thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE8E8E8)),
                        error = painterResource(id = R.drawable.ic_default_image),
                        fallback = painterResource(id = R.drawable.ic_default_image)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.artist.nickname,
                            style = CommitTypography.labelSmall,
                            color = Color(0xFFB0B0B0)
                        )
                        Text(
                            text = item.title,
                            style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color.Black
                        )
                        Text(
                            text = "${item.price}P",
                            style = CommitTypography.bodyMedium,
                            color = Color(0xFF4D4D4D)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "작가에게 문의하기",
                                style = CommitTypography.labelSmall,
                                color = Color(0xFF4D4D4D)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.artist_vector),
                                contentDescription = "문의 화살표",
                                tint = Color(0xFF4D4D4D),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                if (isInProgress || isPending) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "수락",
                            style = CommitTypography.labelSmall,
                            color = if (item.status == "ACCEPTED") Color(0xFF17D5C6) else Color(
                                0xFFB0B0B0
                            )
                        )
                        Text(
                            text = "진행중",
                            style = CommitTypography.labelSmall,
                            color = if (item.status == "IN_PROGRESS") Color(0xFF17D5C6) else Color(
                                0xFFB0B0B0
                            )
                        )
                        Text(
                            text = "작업완료",
                            style = CommitTypography.labelSmall,
                            color = if (item.status == "DONE") Color(0xFF17D5C6) else Color(
                                0xFFB0B0B0
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(Color(0xFFE8E8E8))
                    ) {
                        val barWidth = when (item.status) {
                            "PENDING" -> 0f
                            "ACCEPTED" -> 0f
                            "IN_PROGRESS" -> 0.47f
                            "DONE" -> 1f
                            else -> 0f
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = barWidth)
                                .height(4.dp)
                                .background(Color(0xFF17D5C6))
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RequestCardPreview() {
    val sampleItem = RequestItem(
        requestId = 1,
        status = "PENDING",
        title = "낙서 타입 커미션",
        price = 16000,
        thumbnailImage = "https://via.placeholder.com/150",
        artist = Artist(1, "사과")
    )
    RequestCard(item = sampleItem, onClick = {})
}
