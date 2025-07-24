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
            .imePadding() // 키보드 대응
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

        // 메시지 목록만 스크롤 가능
        ChatMessageList(
            messages = viewModel.chatMessages,
            onPayClick = onPayClick,
            modifier = Modifier
                .weight(1f) // 중요: 메시지 영역만 확장됨
                .fillMaxWidth()
        )

        // 항상 하단 고정 입력창 (파일 메뉴 포함)
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
    // 더미 데이터 (ViewModel 대체용)
    var dummyMessage by remember { mutableStateOf("") }
    var dummyMenuOpen by remember { mutableStateOf(false) }

    val dummyMessages = listOf(
        ChatMessage("1", "system", "커미션 신청", System.currentTimeMillis(), MessageType.SYSTEM, null),
        ChatMessage("2", "artist", "수락합니다!", System.currentTimeMillis(), MessageType.TEXT, null),
        ChatMessage("3", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 40000)
    )

    CommitTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .imePadding() // 키보드 대응
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
