package com.example.commit.ui.post.components

import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.activity.CommissionFormActivity
import com.example.commit.fragment.FragmentPostChatDetail
import com.example.commit.ui.Theme.CommitTypography
import android.os.Bundle

@Composable
fun PostHeaderSection(
    title: String,
    tags: List<String>,
    minPrice: Int,
    summary: String,
    imageCount: Int = 3,
    currentIndex: Int = 0
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 90.dp)
        ) {
            // 상단 바
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .background(Color.White)
            ) {
                val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

                Image(
                    painter = painterResource(id = R.drawable.ic_left_vector),
                    contentDescription = "뒤로가기",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 20.dp)
                        .offset(y = 12.dp)
                        .size(24.dp)
                        .clickable {
                            backDispatcher?.onBackPressed()
                        }
                )

                // 텍스트도 약간 아래로
                Text(
                    text = title,
                    style = CommitTypography.headlineSmall,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 썸네일
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(233.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // 인디케이터
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(imageCount) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(8.dp)
                            .background(
                                color = if (index == currentIndex) Color.Black else Color.LightGray,
                                shape = RoundedCornerShape(50)
                            )
                    )
                }
            }

            // 본문 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = CommitTypography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_unselect_bookmarket),
                        contentDescription = "북마크",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 태그 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.forEachIndexed { index, tag ->
                        val isCategory = index == 0
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .wrapContentWidth()
                                .background(
                                    color = if (isCategory) Color(0xFFE6FFFB) else Color(0xFFF2F2F2),
                                    shape = RoundedCornerShape(50)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isCategory) Color(0xFF17D5C6) else Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                style = CommitTypography.labelSmall,
                                color = if (isCategory) Color(0xFF17D5C6) else Color(0xFFB0B0B0)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = String.format("%,dP~", minPrice),
                    style = CommitTypography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = summary,
                    style = CommitTypography.bodyMedium,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            PostDetailTabSection(onTabSelected = {})
        }

        Spacer(modifier = Modifier.height(24.dp))

        PostBottomBar(
            isRecruiting = true,
            remainingSlots = 11,
            onApplyClick = { 
                val intent = Intent(context, CommissionFormActivity::class.java)
                Log.d("PostScreen", "Intent 생성됨: $intent")
                context.startActivity(intent)
                Log.d("PostScreen", "startActivity 호출됨") 
            },
            onChatClick = { 
                // 채팅방으로 전환 (안전한 방법)
                Log.d("PostScreen", "채팅 버튼 클릭됨")
                
                // Toast 메시지로 피드백 제공
                android.widget.Toast.makeText(
                    context,
                    "채팅방으로 이동합니다",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                
                // Fragment 전환 (안전하게)
                try {
                    val fragment = FragmentPostChatDetail().apply {
                        arguments = Bundle().apply {
                            putString("chatName", title)
                            putString("authorName", "키르")
                        }
                    }
                    
                    if (context is androidx.fragment.app.FragmentActivity) {
                        context.supportFragmentManager.beginTransaction()
                            .replace(com.example.commit.R.id.Nav_Frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                } catch (e: Exception) {
                    Log.e("PostScreen", "채팅방 전환 실패", e)
                    android.widget.Toast.makeText(
                        context,
                        "채팅방 전환에 실패했습니다",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }
}
