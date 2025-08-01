package com.example.commit.ui.FormCheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem

@Composable
fun FormCheckDialog(
    chatItem: ChatItem,
    requestItem: RequestItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = MaterialTheme.shapes.medium,
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 헤더
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "신청서 확인",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Text("✕")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // FormCheckScreen 내용 (간단한 버전)
                FormCheckSimpleContent(
                    chatItem = chatItem,
                    requestItem = requestItem,
                    formSchema = formSchema,
                    formAnswer = formAnswer
                )
            }
        }
    }
}

@Composable
private fun FormCheckSimpleContent(
    chatItem: ChatItem,
    requestItem: RequestItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>
) {
    Column {
        // 커미션 정보
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = requestItem.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "가격: ${requestItem.price}원",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "작가: ${requestItem.artist.nickname}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 신청서 내용
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "신청서 내용",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                formSchema.forEach { item ->
                    when (item.type) {
                        "text" -> {
                            val answer = formAnswer[item.label] as? String ?: ""
                            Text(
                                text = "${item.label}: $answer",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        else -> {
                            Text(
                                text = "${item.label}: ${formAnswer[item.label]}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
} 