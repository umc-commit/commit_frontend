package com.example.commit.ui.request.components

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.activity.ReviewWriteActivity
import com.example.commit.data.model.Artist
import com.example.commit.data.model.RequestItem

@Composable
fun RequestDetailItem(item: RequestItem) {
    val context = LocalContext.current
    val isInProgress = item.status == "IN_PROGRESS" || item.status == "ACCEPTED"
    val statusText = if (isInProgress) "진행 중" else "거래 완료"
    val statusColor = if (isInProgress) Color(0xFF17D5C6) else Color.Black

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
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "25.05.01(목) 작업 완료",
                fontSize = 12.sp,
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
                Text("${item.artist.nickname}", fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("${item.price}P", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 작업물 확인 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isInProgress) Color(0xFFE8E8E8) else Color.Black)
                .let {
                    if (!isInProgress) it.clickable {
                        // TODO: 작업물 확인 클릭 동작
                    } else it
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "작업물 확인",
                color = if (isInProgress) Color.Gray else Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 하단 버튼 3개
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("거래완료", "후기작성", "문의하기").forEachIndexed { index, label ->
                val isFirst = index == 0
                val disableFirst = isFirst && !isInProgress

                val buttonBackground = if (disableFirst) Color(0xFFEDEDED) else Color(0xFFF0F0F0)
                val textColor = if (disableFirst) Color(0xFFB0B0B0) else Color.Black

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
                    Text(label, fontSize = 13.sp, color = textColor)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRequestDetailItem() {
    val dummyItem = RequestItem(
        requestId = 1,
        status = "DONE",
        title = "2인 캐릭터 세트",
        price = 16000,
        thumbnailImage = "",
        artist = Artist(id = 1, nickname = "감자")
    )
    RequestDetailItem(item = dummyItem)
}
