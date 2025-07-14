package com.example.commit.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun ChatEditTopBar(
    onSettingClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // "채팅" 텍스트
        Text(
            text = "채팅",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier
                .padding(start = 183.dp, top = 45.dp, bottom = 25.dp)
        )

        // 설정 아이콘
        IconButton(
            onClick = onSettingClick,
            modifier = Modifier
                .padding(start = 353.dp, top = 49.dp) // 정확히 위치 지정
                .size(18.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "설정",
                modifier = Modifier.size(18.dp),
                tint = Color.Unspecified
            )
        }
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 100)
@Composable
fun ChatEditTopBarPreview() {
    CommitTheme {
        ChatEditTopBar()
    }
}