package com.example.commit.ui.chatroom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R

@Composable
fun FileOptionMenu(
    onGalleryClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    onFileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterHorizontally)
    ) {
        FileOptionItem(
            iconRes = R.drawable.ic_album,
            label = "앨범",
            onClick = onGalleryClick
        )

        FileOptionItem(
            iconRes = R.drawable.ic_chatcamera,
            label = "카메라",
            onClick = onCameraClick
        )

        FileOptionItem(
            iconRes = R.drawable.ic_file,
            label = "파일",
            onClick = onFileClick
        )
    }
}

@Composable
fun FileOptionItem(iconRes: Int, label: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(width = 55.dp, height = 77.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFileOptionMenu() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FileOptionMenu(
            onGalleryClick = { println("앨범 클릭") },
            onCameraClick = { println("카메라 클릭") },
            onFileClick = { println("파일 클릭") }
        )
    }
}
