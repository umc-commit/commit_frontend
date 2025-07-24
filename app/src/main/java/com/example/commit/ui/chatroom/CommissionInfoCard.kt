package com.example.commit.ui.chatroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.example.commit.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommissionInfoCard(
    title: String = "낙서 타입 커미션",
    period: String = "작업기간 : 23시간",
    onSeePostClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // 커미션 박스
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 28.dp, end = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 썸네일
            Image(
                painter = painterResource(id = R.drawable.bg_thumbnail_rounded),
                contentDescription = "썸네일 이미지",
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray), // 썸네일 배경 (임시)
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 텍스트 그룹
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF222222),
                    fontFamily = FontFamily(Font(R.font.notosanskr_semibold))
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            // 글보기 버튼
            IconButton(
                onClick = onSeePostClick,
                modifier = Modifier
                    .size(width = 48.dp, height = 25.dp)
                    .background(
                        color = Color.Transparent,
                        shape = RoundedCornerShape(6.dp)

                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_seepost_black),
                    contentDescription = "글보기",
                    tint = Color.Unspecified
                )
            }
        }

        // 하단 구분선
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            color = Color(0xFFE8E8E8),
            thickness = 1.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommissionInfoCard() {
    CommissionInfoCard(
        title = "낙서 타입 커미션",
        period = "작업기간 : 23시간",
        onSeePostClick = { println("글보기 버튼 클릭됨") }
    )
}


