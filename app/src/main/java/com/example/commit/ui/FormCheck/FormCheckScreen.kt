package com.example.commit.ui.FormCheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem

@Composable
fun FormCheckScreen(
    chatItem: ChatItem,
    requestItem: RequestItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 상단 고정 TopBar
        FormCheckTopBar(
            chatItem = chatItem,
            requestItem = requestItem,
            onBackClick = onBackClick
        )

        // 스크롤 가능한 Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
                .verticalScroll(rememberScrollState())
        ) {
            FormCheckSection(
                item = chatItem,
                formSchema = formSchema,
                formAnswer = formAnswer,
                onBackClick = onBackClick
            )
        }
    }
}
