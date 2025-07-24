package com.example.commit.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.Theme.notoSansKR
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ChatDeleteScreen(
    chatItems: List<ChatItem>,
    selectedItems: List<ChatItem>,
    onItemToggle: (ChatItem) -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 상단바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(24.dp)) // 가운데 정렬 위한 placeholder

            Text(
                text = "채팅",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = notoSansKR,
                color = Color.Black
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.Black
                )
            }
        }

        // 채팅 리스트
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 28.dp)
        ) {
            items(chatItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { onItemToggle(item) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundedCheckbox(
                        checked = selectedItems.contains(item),
                        onCheckedChange = { onItemToggle(item) }
                    )


                    Spacer(modifier = Modifier.width(12.dp))

                    ChatListItem(item = item, showNewIndicator = false)
                }
                Divider(color = Color(0xFFE8E8E8), thickness = 1.dp)
            }
        }

        // 삭제 버튼
        val isEnabled = selectedItems.isNotEmpty()

        Button(
            onClick = onDeleteClick,
            enabled = isEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEnabled) Color(0xFF4D4D4D) else Color(0xFFEDEDED),
                contentColor = Color.Unspecified
            )
        ) {
            Text(
                text = "삭제하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (isEnabled) Color.White else Color(0xFFB0B0B0)
            )
        }
    }
}

@Composable
fun RoundedCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val borderColor = if (checked) Color(0xFF17D5C6.toInt()) else Color.LightGray
    val backgroundColor = if (checked) Color(0xFF17D5C6) else Color.Transparent
    val checkmarkColor = Color(0xFF17D5C6)

    Box(
        modifier = Modifier
            .size(18.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(4.dp))
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Checked",
                tint = checkmarkColor,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ChatDeleteScreenPreview() {
    val sampleChats = listOf(
        ChatItem(R.drawable.ic_profile, "키르", "[결제 요청] 낙서 타임 커미션", "방금 전", true),
        ChatItem(R.drawable.ic_profile, "브로콜리", "[커미션 완료] 일러스트 타입", "2일 전", false)
    )
    val selected = remember { mutableStateListOf<ChatItem>() }

    CommitTheme {
        ChatDeleteScreen(
            chatItems = sampleChats,
            selectedItems = selected,
            onItemToggle = {
                if (selected.contains(it)) selected.remove(it) else selected.add(it)
            },
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}