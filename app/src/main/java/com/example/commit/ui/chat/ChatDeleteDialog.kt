package com.example.commit.ui.chat

import android.R.attr.text
import android.R.attr.top
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.CommitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteOptionBottomSheet(
    onDismiss: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFFFFFFF), // 내용 영역 배경
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Text(
                text = "채팅방 삭제",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDeleteClick()
                        onDismiss()
                    }
            )
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 130)
@Composable
fun DeleteOptionTestPreview() {
    CommitTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "채팅방 삭제",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable { }
            )
        }
    }
}

