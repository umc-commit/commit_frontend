package com.example.commit.ui.FormCheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.Theme.notoSansKR

@Composable
fun FormCheckTopBar(
    onBackClick: () -> Unit,
    chatItem: ChatItem,
    requestItem: RequestItem,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 28.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // 왼쪽: 버튼 + 텍스트 그룹
            Column {
                Button(
                    modifier = Modifier
                        .height(20.dp)
                        .width(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFFE8FBFA)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(Color(0xFF17D5C6))
                    ),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    onClick = {}
                ) {
                    Text(
                        text = "신청완료",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0DD3BF)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "신청시간 ${requestItem.createdAt}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = notoSansKR,
                    color = Color(0xFF4D4D4D)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${chatItem.name}님의",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = notoSansKR,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chatItem.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = notoSansKR,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 오른쪽: 닫기 버튼
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_x),
                    contentDescription = "닫기",
                    tint = Color.Black
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FormCheckTopBarPreview() {
    CommitTheme {
        val dummyChatItem = ChatItem(
            profileImageRes = com.example.commit.R.drawable.ic_profile,
            profileImageUrl = null,
            name = "키르",
            message = "최근 메시지",
            time = "2시간 전",
            isNew = false,
            title = "낙서 타임 커미션"
        )
        
        val dummyRequestItem = RequestItem(
            requestId = 1,
            status = "진행중",
            title = "낙서 타임 커미션",
            price = 50000,
            thumbnailImageUrl = "",
            progressPercent = 50,
            createdAt = "2023-12-01",
            artist = com.example.commit.data.model.Artist(
                id = 1,
                nickname = "키르"
            ),
            commission = com.example.commit.data.model.Commission(
                id = 1
            )
        )
        
        FormCheckTopBar(
            onBackClick = {},
            chatItem = dummyChatItem,
            requestItem = dummyRequestItem
        )
    }
}

