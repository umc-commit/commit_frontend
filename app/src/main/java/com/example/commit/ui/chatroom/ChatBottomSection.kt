package com.example.commit.ui.chatroom

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

@Composable
fun ChatBottomSection(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isMenuOpen: Boolean,
    onToggleMenu: () -> Unit
) {
    Column {
        if (isMenuOpen) {
            FileOptionMenu()
        }

        ChatInputBar(
            message = message,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            onPlusClick = onToggleMenu
        )
    }
}
