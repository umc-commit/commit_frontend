package com.example.commit.ui.post.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun PostBottomBar(
    modifier: Modifier = Modifier,
    isRecruiting: Boolean,
    remainingSlots: Int = 0,
    onApplyClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        // 슬롯 상태 아이콘
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 20.dp)
                .offset(x = 15.dp, y = (-28).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (isRecruiting) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .offset(x = 35.dp, y = 0.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_form),
                            contentDescription = "슬롯 아이콘",
                            tint = Color.Unspecified,
                            modifier = Modifier.fillMaxSize()
                        )

                        Text(
                            text = "남은 슬롯 ${remainingSlots}개",
                            style = CommitTypography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.offset(y = (-2).dp)
                        )
                    }

                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_compelete_form),
                        contentDescription = "마감 아이콘",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(75.dp)
                            .offset(x = 35.dp, y = 0.dp)
                    )
                }
            }
        }

        // 하단 버튼
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 신청하기
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(
                        color = if (isRecruiting) Color(0xFF333333) else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )

                    .clickable { onApplyClick() },
                    contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_request_box),
                        contentDescription = "신청 아이콘",
                        tint = if (isRecruiting) Color.White else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRecruiting) "신청하기" else "신청불가",
                        style = CommitTypography.bodyMedium,
                        color = if (isRecruiting) Color.White else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 채팅하기
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(Color(0xFF333333), RoundedCornerShape(12.dp))
                    .clickable { 
                        Log.d("PostBottomBar", "채팅하기 버튼 클릭됨!")
                        onChatClick() 
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chat),
                        contentDescription = "채팅 아이콘",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "채팅하기",
                        style = CommitTypography.bodyMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewPostBottomBar() {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))
        PostBottomBar(
            isRecruiting = true,
            remainingSlots = 3,
            onApplyClick = {},
            onChatClick = {}
        )
    }
}
