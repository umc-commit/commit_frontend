package com.example.commit.ui.chatlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.data.model.entities.ChatItem
import androidx.compose.material3.*
import androidx.compose.ui.Alignment

@Composable
fun ChatListItem(item: ChatItem,showNewIndicator: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!item.profileImageUrl.isNullOrBlank()) {
            // Coil
            coil.compose.AsyncImage(
                model = item.profileImageUrl,
                contentDescription = "Profile",
                modifier = Modifier.size(48.dp).clip(CircleShape)
            )
        } else {
            // 리소스/기본 아이콘
            Image(
                painter = painterResource(id = item.profileImageRes),
                contentDescription = "Profile",
                modifier = Modifier.size(48.dp).clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.message,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.time,
                fontSize = 10.sp,
                color = Color(0xFFB0B0B0)
            )

            if (item.isNew) {
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color.Red)
                )
            }
        }
    }
}


