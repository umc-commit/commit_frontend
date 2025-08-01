package com.example.commit.ui.request.form

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
fun CommissionTopBar(onBackClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 28.dp, top = 50.dp, bottom = 28.dp), // Figma 기준 위치
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_white),
                    contentDescription = "뒤로가기",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(105.dp))

            Text(
                text = "커미션 신청",
                color = Color.White,
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 100)
@Composable
fun CommissionTopBarPreview() {
    CommitTheme {
        CommissionTopBar()
    }
}
