package com.example.commit.ui.request.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.search.components.Tag

@Composable
fun CommissionCard() {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(162.dp)
            .padding(end = 11.dp, bottom = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 프로필 + 닉네임
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_profile),
                    contentDescription = "프로필",
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "키르",
                    style = CommitTypography.bodyMedium,
                    color = Color(0xFF2B2B2B)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF3F3F3))
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 제목 + 북마크 오른쪽
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "낙서 타임 커미션",
                    style = CommitTypography.bodyMedium,
                    color = Color(0xFF2B2B2B),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_home_bookmark),
                    contentDescription = "북마크",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.height(4.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Tag(text = "그림", isSelected = true, fontSize = 8.sp)
                Tag(text = "#LD", fontSize = 8.sp)
                Tag(text = "#커플", fontSize = 8.sp)
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun CommissionCardPreview() {
    CommissionCard()
}
