package com.example.commit.ui.review

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
fun ReviewTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.White)
    ) {
        Text(
            text = "작성된 후기",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = notoSansKR,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )

        // 왼쪽 정렬
        IconButton(
            onClick = onBackClick, // 전달된 람다 실행
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "뒤로가기"
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 100)
@Composable
fun ReviewTopBarPreview() {
    CommitTheme {
        ReviewTopBar()
    }
}