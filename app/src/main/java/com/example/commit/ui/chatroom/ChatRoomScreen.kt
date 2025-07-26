// ✅ ChatMessageList 연결 완료 버전
package com.example.commit.ui.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chatroom.ChatMessageList
import com.example.commit.viewmodel.ChatViewModel
import androidx.compose.foundation.layout.imePadding

@Composable
fun ChatRoomScreen(
    commissionTitle: String,
    authorName: String,
    onPayClick: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        ChatroomTopBar(
            authorName = authorName,
            averageResponseTime = "평균 30분 이내 응답",
            onBackClick = {},
            onProfileClick = {}
        )

        Spacer(modifier = Modifier.height(4.dp))

        CommissionInfoCard(title = commissionTitle)

        // 메시지 목록
        ChatMessageList(
            messages = viewModel.chatMessages,
            currentUserId = viewModel.currentUserId,
            onPayClick = onPayClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        // 입력창
        ChatBottomSection(
            message = viewModel.message,
            onMessageChange = viewModel::onMessageChange,
            onSendMessage = viewModel::sendMessage,
            isMenuOpen = isMenuOpen,
            onToggleMenu = { isMenuOpen = !isMenuOpen }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatRoomScreen() {
    var dummyMessage by remember { mutableStateOf("") }
    var dummyMenuOpen by remember { mutableStateOf(false) }

    val dummyMessages = listOf(
        ChatMessage("1", "me", "안녕하세요", System.currentTimeMillis(), MessageType.TEXT, null),
        ChatMessage("2", "artist", "반가워요!", System.currentTimeMillis(), MessageType.TEXT, null),
        ChatMessage("3", "me", "25.06.02 17:50", System.currentTimeMillis(), MessageType.COMMISSION_REQUEST, null),
        ChatMessage("4", "artist", "낙서 타임 커미션", System.currentTimeMillis(), MessageType.COMMISSION_ACCEPTED, null),
        ChatMessage("5", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 50000)
    )

    CommitTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            ChatroomTopBar(
                authorName = "사과",
                averageResponseTime = "평균 30분 이내 응답",
                onBackClick = {},
                onProfileClick = {}
            )

            Spacer(modifier = Modifier.height(4.dp))

            CommissionInfoCard(title = "낙서 타임 커미션")

            ChatMessageList(
                messages = dummyMessages,
                currentUserId = "me",
                onPayClick = { println("결제 클릭") },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )

            ChatBottomSection(
                message = dummyMessage,
                onMessageChange = { dummyMessage = it },
                onSendMessage = {
                    println("보낸 메시지: $dummyMessage")
                    dummyMessage = ""
                },
                isMenuOpen = dummyMenuOpen,
                onToggleMenu = { dummyMenuOpen = !dummyMenuOpen }
            )
        }
    }
}
