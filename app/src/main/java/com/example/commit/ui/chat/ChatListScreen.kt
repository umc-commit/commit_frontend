package com.example.commit.ui.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.data.model.ChatItem
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun ChatListScreen(chatItems: List<ChatItem>) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ChatSearchBar(
            query = query,
            onQueryChange = { query = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = Color(0xFFE8E8E8), thickness = 1.dp)

        LazyColumn {
            items(chatItems) { item ->
                ChatListItem(item = item)
                Divider(color = Color(0xFFE8E8E8), thickness = 1.dp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatListScreenPreview() {
    val sampleChats = listOf(
        ChatItem(
            profileImageRes = R.drawable.img_sample1,
            name = "키르",
            message = "[결제 요청] 낙서 타임 커미션",
            time = "방금 전",
            isNew = true
        ),
        ChatItem(
            profileImageRes = R.drawable.img_sample2,
            name = "브로콜리",
            message = "[커미션 완료] 일러스트 타입",
            time = "2일 전",
            isNew = false
        )
    )

    CommitTheme {
        ChatListScreen(chatItems = sampleChats)
    }
}
