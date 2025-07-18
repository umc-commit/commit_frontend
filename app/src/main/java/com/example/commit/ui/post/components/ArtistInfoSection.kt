package com.example.commit.ui.post.components

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R

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
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
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
    followerCount: Int,
    workCount: Int,
    rating: Float,
    recommendRate: Int,
    reviewCount: Int,
    onFollowClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {

        // 프로필 영역
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.Gray, CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = artistName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "팔로워 $followerCount",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = "총 작업수 ",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${workCount}건",
                        fontSize = 11.sp,
                        color = Color(0xFF17D5C6),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    .clickable { onFollowClick() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "팔로우", fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 후기 요약
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "작가의 커미션 후기 ",
                fontSize = 13.sp,
                color = Color.Black
            )
            Text(
                text = "11개",
                fontSize = 13.sp,
                color = Color(0xFF17D5C6),
                fontWeight = FontWeight.Medium
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
                Text(text = String.format("%.1f", rating), fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                Text(text = "$recommendRate%", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "추천해요", fontSize = 12.sp, color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ExpandableItem(
            title = "전체 후기 보기",
            expanded = isExpanded,
            onToggle = { isExpanded = !isExpanded }
        ) {
            // 실제 후기는 추후 구현 예정
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewArtistInfoSection() {
    ArtistInfoSection(
        artistName = "키르",
        followerCount = 32,
        workCount = 11,
        rating = 5.0f,
        recommendRate = 100,
        reviewCount = 3,
        onFollowClick = { }
    )
}
