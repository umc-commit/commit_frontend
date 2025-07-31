package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.data.model.Artist
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.Theme.CommitTypography

@Composable
<<<<<<< Updated upstream
fun RequestDetailItem(item: RequestItem) {
=======
fun RequestDetailItem(
    item: RequestItem,
    onFormAnswerClick: () -> Unit = {}
)
 {
    val context = LocalContext.current
    val isPending = item.status == "PENDING"
>>>>>>> Stashed changes
    val isInProgress = item.status == "IN_PROGRESS" || item.status == "ACCEPTED"
    val isDone = item.status == "DONE"
    val isCancel = item.status == "CANCEL"
    val isReject = item.status == "REJECT"

    val statusText = when {
        isPending -> "수락 대기"
        isInProgress -> "진행 중"
        isCancel -> "신청 취소"
        isReject -> "신청 거절"
        else -> "거래 완료"
    }

    val statusColor = when {
        isInProgress -> Color(0xFF17D5C6)
        isCancel -> Color(0xFFFF4D4D)
        isReject -> Color(0xFFFF4D4D)
        else -> Color.Black
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 상태 + 날짜
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = statusText,
                style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "25.05.01(목) 작업 완료", // TODO: 실제 날짜 반영
                style = CommitTypography.labelSmall,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 썸네일 + 정보
        Row(verticalAlignment = Alignment.CenterVertically) {
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
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "${item.artist.nickname}",
                    style = CommitTypography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = item.title,
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.price}P",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isPending) {
            // 신청서 보기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4D4D4D))
                    .clickable {
                        // TODO: 신청서 보기 동작
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "신청서 보기",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 신청 취소 / 문의하기
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("신청 취소", "문의하기").forEachIndexed { index, label ->
                    val isCancel = label == "신청 취소"

<<<<<<< Updated upstream
                val buttonBackground = if (disableFirst) Color(0xFFEDEDED) else Color(0xFFF0F0F0)
                val textColor = if (disableFirst) Color(0xFFB0B0B0) else Color.Black

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(buttonBackground)
                        .then(if (!disableFirst) Modifier.clickable { /* TODO */ } else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = CommitTypography.labelSmall,
                        color = textColor
                    )
                }
            }
        }
=======
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                            .clickable {
                                // TODO: 클릭 동작
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = CommitTypography.labelSmall,
                            color = if (isCancel) Color(0xFFFF4D4D) else Color.Black
                        )
                    }
                }
            }
        }
        else if(isCancel || isReject){
            // 신청서 보기 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4D4D4D))
                    .clickable {
                        onFormAnswerClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "신청서 보기",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        else {
            // 작업물 확인 버튼
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4D4D4D))
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "작업물 확인",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            // 기존 하단 버튼 3개 (거래완료 / 후기작성 / 문의하기)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("거래완료", "후기작성", "문의하기").forEachIndexed { index, label ->
                    val isFirst = index == 0
                    val disableFirst = isFirst && !isInProgress

                    val buttonBackground = if (disableFirst) Color(0xFFEDEDED) else Color(0xFFF0F0F0)

                    val textColor = when {
                        label == "후기작성" && isDone -> Color(0xFF0DD3BF)
                        disableFirst -> Color(0xFFB0B0B0)
                        else -> Color.Black
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(buttonBackground)
                            .let {
                                if (!disableFirst) {
                                    it.clickable {
                                        when (label) {
                                            "후기작성" -> {
                                                val intent = Intent(context, ReviewWriteActivity::class.java)
                                                intent.putExtra("requestId", item.requestId)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                            }
                                            "거래완료" -> {
                                                // TODO: 거래완료 처리
                                            }
                                            "문의하기" -> {
                                                // TODO: 문의하기 처리
                                            }
                                        }
                                    }
                                } else it
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = CommitTypography.labelSmall,
                            color = textColor
                        )
                    }
                }
            }
        }
>>>>>>> Stashed changes
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRequestDetailItem() {
    val dummyItem = RequestItem(
        requestId = 1,
        status = "REJECT",
        title = "낙서 타입 커미션",
        price = 40000,
        thumbnailImage = "",
        artist = Artist(id = 1, nickname = "키르")
    )
    RequestDetailItem(item = dummyItem)
}
