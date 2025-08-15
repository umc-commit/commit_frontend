package com.example.commit.ui.request.components

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.activity.ReviewWriteActivity
import com.example.commit.connection.dto.CommissionItem
import com.example.commit.connection.dto.RequestItem
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.activity.FormCheckActivity

@Composable
fun RequestDetailItem(
    item: RequestItem,
    commission: CommissionItem,
    totalPrice: Int,
    // 폼/답변을 JSON으로 넘길 때 사용(없으면 null)
    formSchemaJson: String? = null,
    formAnswerJson: String? = null,
    createdAt: String? = null
) {
    val context = LocalContext.current
    val status = item.status.trim()

    val isPending = status == "PENDING"
    val isInProgress = status == "IN_PROGRESS" || status == "APPROVED"
    val isSubmitted = status == "SUBMITTED"
    val isCompleted = status == "COMPLETED"
    val isCancel = status == "CANCELED"
    val isReject = status == "REJECTED"

    val statusText = when {
        isPending -> "수락 대기"
        isInProgress -> "진행 중"
        isSubmitted -> "작업 완료"
        isCompleted -> "거래 완료"
        isCancel -> "신청 취소"
        isReject -> "신청 거절"
        else -> "오류"
    }

    val statusColor = when {
        isInProgress || isSubmitted -> Color(0xFF17D5C6)
        isCancel || isReject -> Color(0xFFFF4D4D)
        else -> Color.Black
    }

    /* [기능] FormCheck 화면 열기 (컨텍스트 유형에 따라 NEW_TASK 자동 적용) */
    fun openFormCheckSafe(ctx: Context) {
        val intent = Intent(ctx, FormCheckActivity::class.java).apply {
            putExtra("requestId", item.requestId)
            putExtra("commissionId", commission.id)
            putExtra("title", commission.title)
            putExtra("artistId", commission.artist.id)
            putExtra("artistNickname", commission.artist.nickname)
            putExtra("thumbnailUrl", commission.thumbnailImageUrl ?: "")
            putExtra("totalPrice", totalPrice)
            putExtra("createdAt", createdAt ?: "")
            putExtra("formSchemaJson", formSchemaJson)
            putExtra("formAnswerJson", formAnswerJson)
        }

        // Activity 컨텍스트면 바로 실행, 아니면 NEW_TASK로 실행
        if (ctx is Activity) {
            ctx.startActivity(intent)
        } else {
            ctx.applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 상태
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = statusText,
                style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = statusColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 썸네일 + 기본 정보
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = commission.thumbnailImageUrl,
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
                    text = commission.artist.nickname,
                    style = CommitTypography.labelSmall,
                    color = Color.Gray
                )
                Text(
                    text = commission.title,
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${totalPrice}P",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isPending) {
            // 신청서 보기
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4D4D4D))
                    .clickable { openFormCheckSafe(context) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "신청서 보기",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 신청 취소 / 문의하기 - 임시: 문의하기도 폼 확인으로 연결
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("신청 취소", "문의하기").forEach { label ->
                    val isCancelLabel = label == "신청 취소"

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0F0F0))
                            .clickable { openFormCheckSafe(context) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = CommitTypography.labelSmall,
                            color = if (isCancelLabel) Color(0xFFFF4D4D) else Color.Black
                        )
                    }
                }
            }
        } else if (isCancel || isReject) {
            // 신청서 보기 (취소/거절 상태)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF4D4D4D))
                    .clickable { openFormCheckSafe(context) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "신청서 보기",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        } else {
            // 작업물 확인(추후 기능)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isInProgress) Color(0xFFF1F1F1) else Color(0xFF4D4D4D)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "작업물 확인",
                    style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isInProgress) Color(0xFFB0B0B0) else Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 작업완료 / 후기작성 / 문의하기
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("작업완료", "후기작성", "문의하기").forEachIndexed { index, label ->
                    val isFirst = index == 0
                    val disableFirst = isFirst && !isInProgress

                    val backgroundColor = if (disableFirst) Color(0xFFEDEDED) else Color(0xFFF0F0F0)
                    val textColor = when {
                        label == "후기작성" && isCompleted -> Color(0xFF0DD3BF)
                        disableFirst -> Color(0xFFB0B0B0)
                        else -> Color.Black
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .let {
                                if (!disableFirst) {
                                    it.clickable {
                                        when (label) {
                                            "후기작성" -> {
                                                val intent = Intent(context, ReviewWriteActivity::class.java)
                                                intent.putExtra("requestId", item.requestId)
                                                // 여기서는 기존 로직 유지(NEW_TASK로 안전)
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                            }
                                            "작업완료" -> {
                                                // TODO: 작업완료 처리
                                            }
                                            "문의하기" -> {
                                                openFormCheckSafe(context)
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
    }
}
