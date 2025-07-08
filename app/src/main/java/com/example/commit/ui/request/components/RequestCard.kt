package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.data.model.Artist
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.request.notoSansKR
import androidx.compose.ui.graphics.Color

@Composable
fun RequestCard(item: RequestItem) {
    val isDone = item.status == "DONE"
    val isInProgress = !isDone

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = 2.dp,
        backgroundColor = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isInProgress) "진행 중" else "작업 완료",
                fontSize = 14.sp,
                fontFamily = notoSansKR,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                AsyncImage(
                    model = item.thumbnailImage,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8E8E8)), // gray1
                    error = painterResource(id = R.drawable.ic_default_image),
                    fallback = painterResource(id = R.drawable.ic_default_image)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.artist.nickname,
                        fontSize = 12.sp,
                        color = Color(0xFFB0B0B0),
                        fontFamily = notoSansKR
                    )
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontFamily = notoSansKR,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${item.price}P",
                        fontSize = 14.sp,
                        color = Color(0xFF4D4D4D),
                        fontFamily = notoSansKR,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "작가에게 문의하기 >",
                        fontSize = 12.sp,
                        color = Color(0xFF222222),
                        fontFamily = notoSansKR,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            if (isInProgress) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "수락",
                        fontSize = 12.sp,
                        color = if (item.status == "ACCEPTED") Color(0xFF17D5C6) else Color(0xFFB0B0B0)
                    )
                    Text(
                        text = "진행중",
                        fontSize = 12.sp,
                        color = if (item.status == "IN_PROGRESS") Color(0xFF17D5C6) else Color(0xFFB0B0B0)
                    )
                    Text(
                        text = "작업완료",
                        fontSize = 12.sp,
                        color = if (item.status == "DONE") Color(0xFF17D5C6) else Color(0xFFB0B0B0)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFFE8E8E8))
                ) {
                    val barWidth = when (item.status) {
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

@Preview(showBackground = true)
@Composable
fun RequestCardPreview() {
    val sampleItem = RequestItem(
        requestId = 1,
        status = "DONE",
        title = "낙서 타입 커미션",
        price = 16000,
        thumbnailImage = "https://via.placeholder.com/150",
        artist = Artist(1, "사과")
    )
    RequestCard(item = sampleItem)
}
