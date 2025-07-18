package com.example.commit.ui.post.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
fun ReviewItem(
    rating: Float,
    title: String,
    duration: String,
    content: String,
    writer: String,
    date: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "별점",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$rating", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = "작업기간: $duration", fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = content, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(text = writer, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = date, fontSize = 12.sp, color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color(0xFFE0E0E0))
    }
}

@Composable
fun ReviewListSection() {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        ReviewItem(
            rating = 5.0f,
            title = "낙서 타임 커미션",
            duration = "12시간",
            content = "친절하게 응대해주셨습니다. 감사해요!",
            writer = "워시",
            date = "2일 전"
        )
        ReviewItem(
            rating = 4.0f,
            title = "2인 커플 커미션",
            duration = "3일",
            content = "복잡한 의상이었는데도 디테일 살려서 잘 그려주셨어요 :)",
            writer = "히터",
            date = "4일 전"
        )
        ReviewItem(
            rating = 5.0f,
            title = "낙서 타임 커미션",
            duration = "20시간",
            content = "답장도 빠르고 잘 대해주셨어요. 감사합니다 !",
            writer = "미루",
            date = "5일 전"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReviewListSection() {
    ReviewListSection()
}
