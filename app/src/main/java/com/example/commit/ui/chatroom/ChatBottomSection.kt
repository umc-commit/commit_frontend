package com.example.commit.ui.chatroom

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ChatBottomSection(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isMenuOpen: Boolean,
    onToggleMenu: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {


        ChatInputBar(
            message = message,
            onMessageChange = onMessageChange,
            onSendMessage = onSendMessage,
            onPlusClick = onToggleMenu
        )

        AnimatedVisibility(
            visible = isMenuOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            FileOptionMenu()
        }
    }
}
