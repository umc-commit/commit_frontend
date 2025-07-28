package com.example.commit.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.data.model.entities.Review
import com.example.commit.ui.Theme.notoSansKR

@Composable
fun ReviewItem(review: Review,
               onDeleteClick: () -> Unit = {},
               onEditClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 20.dp, horizontal = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_thumbnail_rounded),
                    contentDescription = stringResource(id = R.string.thumbnail),
                    modifier = Modifier
                        .size(width = 80.dp, height = 60.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.nickname,
                        fontSize = 10.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = review.title,
                        fontSize = 14.sp,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )

                }
                Text(
                    text = review.duration,
                    fontSize = 10.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(modifier = Modifier.padding(start = 4.dp)) {
                repeat(5) { index ->
                    val starRes = if (index < review.rating) {
                        R.drawable.ic_star_on  // 채워진 별
                    } else {
                        R.drawable.ic_star_off // 빈 별
                    }
                    Image(
                        painter = painterResource(id = starRes),
                        contentDescription = null,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),

            ) {
                Text(
                    text = review.content,
                    fontSize = 10.sp,
                    fontFamily = notoSansKR,
                    color = Color(0xFF222222),
                    modifier = Modifier.weight(1f)
                        .padding(top = 10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.bg_thumbnail_rounded),
                    contentDescription = stringResource(id = R.string.mini_thumbnail),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier
                        .height(22.dp)
                        .width(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color(0xFFB0B0B0))
                    )
                ) {
                    Text(
                        text = "수정",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4D4D4D)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .height(22.dp)
                        .width(48.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color(0xFFB0B0B0))
                    )
                ) {
                    Text(
                        text = "삭제",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4D4D4D)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

    }
    HorizontalDivider(color = Color(0xFFEBEBEB), thickness = 1.dp)
}

@Preview(showBackground = true)
@Composable
fun ReviewItemPreview() {
    ReviewItem(
        review = Review(
            id = 1,
            nickname = "키르",
            title = "낙서 타입 커미션",
            content = "요청사항도 잘 들어주시고 마감도 빠르게 해주셨어요! 감사합니다.",
            duration = "작업기간 : 23시간",
            rating = 4
        )
    )
}
