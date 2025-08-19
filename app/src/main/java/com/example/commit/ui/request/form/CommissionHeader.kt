package com.example.commit.ui.request.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun CommissionHeader(
    artistName: String = "키르",
    commissionTitle: String = "낙서 타입 커미션",
    thumbnailImageUrl: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 28.dp, top = 20.dp) // Figma 여백 기준
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically

    ) {

        // 프로필 이미지
        if (!thumbnailImageUrl.isNullOrBlank()) {
            AsyncImage(
                model = thumbnailImageUrl,
                contentDescription = "프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 80.dp, height = 60.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        } else {
            // URL 없을 때 플레이스홀더
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 60.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = Color(0xFFD9D9D9))
            )
        }

        Spacer(modifier = Modifier.width(14.dp)) // Figma 기준 간격

        Column {
            Text(
                text = "${artistName}님의",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(11.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = commissionTitle,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.width(6.dp)) // 가로 간격

                Text(
                    text = "신청 폼",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommissionHeaderPreview() {
    CommitTheme {
        CommissionHeader()
    }
}
