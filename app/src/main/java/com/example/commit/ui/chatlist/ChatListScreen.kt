package com.example.commit.ui.chatlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun ChatListScreen(
    chatItems: List<ChatItem>,
    isLoading: Boolean = false,
    onItemClick: (ChatItem) -> Unit,
    onSettingClick: () -> Unit // 수정: 외부로부터 받는 콜백
) {
    var query by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            ChatEditTopBar(
                onSettingClick = onSettingClick
            )

            ChatSearchBar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                // 로딩 중일 때 표시할 내용
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    )
                }
            } else if (chatItems.isEmpty()) {
                // 데이터가 없을 때 표시할 내용
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "채팅방이 없습니다",
                        color = androidx.compose.ui.graphics.Color(0xFF666666),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn {
                    items(chatItems) { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    item.isNew = false
                                    onItemClick(item)
                                }
                        ) {
                            ChatListItem(item = item, showNewIndicator = item.isNew)
                        }
                        Divider(color = Color(0xFFE8E8E8), thickness = 1.dp)
                    }
                }
            }
        }
    }

}


@Preview(
    name = "Chat List",
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ChatListScreenPreview() {
    val sampleChats = listOf(
        ChatItem(
            profileImageRes = R.drawable.ic_pf_charac2,
            name = "키르",
            message = "[결제 요청] 낙서 타임 커미션",
            time = "방금 전",
            isNew = true,
            title = "낙서 타입 커미션"
        ),
        ChatItem(
            profileImageRes = R.drawable.ic_pf_charac1,
            name = "브로콜리",
            message = "[커미션 완료] 일러스트 타입",
            time = "2일 전",
            isNew = false,
            title = "일러스트 타입 커미션"
        )
    )

    CommitTheme {
        ChatListScreen(
            chatItems = sampleChats,
            isLoading = false,
            onItemClick = {},
            onSettingClick = {} // 필수 파라미터 추가
        )
    }
}
