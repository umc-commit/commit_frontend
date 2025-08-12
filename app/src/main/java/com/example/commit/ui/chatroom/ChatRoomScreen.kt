
package com.example.commit.ui.chatroom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chatroom.ChatMessageList
import com.example.commit.viewmodel.ChatViewModel
import androidx.compose.foundation.layout.imePadding
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.commit.ui.chatroom.ChatroomTopBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ChatRoomScreen(
    commissionTitle: String,
    authorName: String,
    onPayClick: () -> Unit,
    onFormCheckClick: () -> Unit,
    onBackClick: () -> Unit,
    onSettingClick: () -> Unit,
    onProfileClick: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    var isMenuOpen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // 메시지 목록을 안전하게 관리
    val messages by remember(chatViewModel.chatMessages) {
        mutableStateOf(chatViewModel.chatMessages)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(22.dp))

        ChatroomTopBar(
            Name = authorName,
            averageResponseTime = "평균 30분 이내 응답",
            onProfileClick = onProfileClick,
            onBackClick = onBackClick,
            onSettingClick = onSettingClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        CommissionInfoCard(title = commissionTitle)

        // 메시지 목록
        ChatMessageList(
            messages = messages,
            currentUserId = chatViewModel.currentUserId,
            onPayClick = {
                // 결제 완료 시스템 메시지 표시
                chatViewModel.showPaymentCompleteMessage()
                // 기존 onPayClick도 실행 (필요한 경우)
                onPayClick()
            },
            onFormCheckClick = onFormCheckClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        // 입력창
        val context = LocalContext.current
        ChatBottomSection(
            message = chatViewModel.message,
            onMessageChange = chatViewModel::onMessageChange,
            onSendMessage = { 
                chatViewModel.sendMessage(context)
                // 랜덤하게 더미 응답도 생성 (50% 확률)
                if (Math.random() < 0.5) {
                    chatViewModel.generateDummyResponse()
                }
                
                // 신청서가 제출된 경우 커미션 수락 메시지 생성 (임시 테스트용)
                if (chatViewModel.hasSubmittedApplication && Math.random() < 0.3) {
                    coroutineScope.launch {
                        delay(3000L) // 3초 후
                        chatViewModel.showCommissionAcceptedMessage()
                        delay(2000L) // 2초 후
                        chatViewModel.showPaymentRequestMessage(50000)
                    }
                }
            },
            isMenuOpen = isMenuOpen,
            onToggleMenu = { isMenuOpen = !isMenuOpen }
        )
        Spacer(modifier = Modifier.height(69.dp))
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
        ChatMessage("5", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 50000),
        ChatMessage("6", "me", "", System.currentTimeMillis(), MessageType.PAYMENT_COMPLETE, null),
        ChatMessage("7", "artist", "", System.currentTimeMillis(), MessageType.COMMISSION_START, null),
        ChatMessage("8", "artist", "25.06.02 17:50", System.currentTimeMillis(), MessageType.COMMISSION_COMPLETE, null)
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
                averageResponseTime = "평균 30분 이내 응답",
                onProfileClick = {},
                onBackClick = {},
                onSettingClick = {}
            )

            Spacer(modifier = Modifier.height(4.dp))

            CommissionInfoCard(title = "낙서 타임 커미션")

            ChatMessageList(
                messages = dummyMessages,
                currentUserId = "me",
                onPayClick = { println("결제 클릭") },
                onFormCheckClick = { println("신청서 확인하기 클릭") },
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
            Spacer(modifier = Modifier.height(69.dp))

        }
    }
}
