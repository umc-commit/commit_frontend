package com.example.commit.ui.chatroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.commit.ui.Theme.CommitTheme
import android.content.Intent
import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChatroomTopBar(
    Name: String = "사과",
    averageResponseTime: String = "평균 30분 이내 응답",
    artistId: Int = -1,  // 작가 ID 추가
    onProfileClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingClick: () -> Unit = {}
) {
    val context = LocalContext.current  // Context를 여기서 가져오기
    Column {
        // 상단 TopBar 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // 좌측: 뒤로가기 + 작가 정보
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp) // 터치 영역 확보
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_chatback),
                        contentDescription = "Back",
                        tint = Color(0xFF4D4D4D),
                        modifier = Modifier.size(22.dp) // 실제 아이콘 크기
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 작가 이름
                        Text(
                            text = Name,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(end = 6.dp)
                        )

                        // 작가 태그
                        Surface(
                            color = Color(0xFFF4E1FF),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "작가",
                                color = Color(0xFFD072EC),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        // 작가 프로필 이동 버튼
                        IconButton(
                            onClick = {
                                // AuthorProfileActivity로 이동
                                val intent = Intent(context, Class.forName("com.example.commit.activity.author.AuthorProfileActivity"))
                                if (artistId != -1) {
                                    // 유효한 artistId가 있을 때만 전달
                                    intent.putExtra("artistId", artistId)
                                    Log.d("ChatroomTopBar", "작가 프로필 이동 - artistId: $artistId")
                                } else {
                                    Log.d("ChatroomTopBar", "작가 프로필 이동 - artistId 없음, 기본 프로필 화면으로 이동")
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .size(24.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow_right),
                                contentDescription = "Go to Profile",
                                modifier = Modifier.size(width = 4.dp, height = 8.dp),
                                tint = Color(0xFFB0B0B0)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    // 평균 응답 시간
                    Text(
                        text = averageResponseTime,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = Color(0xFFB0B0B0)
                        )
                    )
                }
            }

            // 우측: 검색 + 메뉴 아이콘
            Row {
                IconButton(
                    onClick = { /* TODO: Search */ },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_darkgray),
                        contentDescription = "Search",
                        tint = Color(0xFF4D4D4D),
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onSettingClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_movevert),
                        contentDescription = "More",
                        tint = Color(0xFF4D4D4D),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Divider()
    }
}
@Preview(showBackground = true)
@Composable
fun ChatroomTopBarPreview() {
    CommitTheme {
        ChatroomTopBar(
            averageResponseTime = "평균 30분 이내 응답",
            artistId = 123,  // Preview용 artistId 추가
            onBackClick = {},
            onProfileClick = {}
        )
    }
}
