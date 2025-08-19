package com.example.commit.ui.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
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
import com.example.commit.ui.Theme.notoSansKR
@Composable
fun ChatEditTopBar(
    modifier: Modifier = Modifier,
    onSettingClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.White)
    ) {
        Text(
            text = "채팅",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = notoSansKR,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        // 오른쪽 정렬
        IconButton(
            onClick = onSettingClick, // 전달된 람다 실행
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_setting),
                contentDescription = "설정"
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 100)
@Composable
fun ChatEditTopBarPreview() {
    CommitTheme {
        ChatEditTopBar()
    }
}