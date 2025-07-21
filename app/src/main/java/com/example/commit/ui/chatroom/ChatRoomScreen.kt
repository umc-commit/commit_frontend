package com.example.commit.ui.chatroom

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.viewmodel.ChatViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun ChatRoomScreen(
    commissionTitle: String,
    onPayClick: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        ChatroomTopBar(title = commissionTitle)

        CommissionInfoCard(title = commissionTitle)

        ChatMessageList(
            messages = viewModel.chatMessages,
            onPayClick = onPayClick
        )

        // FileOptionMenu + ChatInputBar 포함
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
fun PreviewCommissionChatScreen() {
    val messages = listOf(
        ChatMessage("1", "system", "커미션 신청", System.currentTimeMillis(), MessageType.SYSTEM, null),
        ChatMessage("2", "artist", "수락합니다!", System.currentTimeMillis(), MessageType.TEXT, null),
        ChatMessage("3", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 40000)
    )

    ChatRoomScreen(
        commissionTitle = "낙서 타임 커미션",
        onPayClick = { println("결제 클릭") }
    )
}
