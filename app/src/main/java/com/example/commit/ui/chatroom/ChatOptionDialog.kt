package com.example.commit.ui.chatroom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.CommitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatOptionDialog(
    onDismiss: () -> Unit,
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
                text = "알림 끄기",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                    }
            )

            Spacer(modifier = Modifier.height(34.dp))
            Text(
                text = "신고하기",
                color = Color(0xFFFF4D4D),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDismiss()
                    }
            )
        }
    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 130)
@Composable
fun ChatOptionDialogPreview() {
    CommitTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "알림 끄기",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable { }
            )
            Spacer(modifier = Modifier.height(34.dp))
            Text(
                text = "신고하기",
                color = Color(0xFFFF4D4D),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .clickable { }
            )
        }
    }
}

