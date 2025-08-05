package com.example.commit.ui.request.form

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun CommissionImageTextSection(
    text: String,
    onTextChange: (String) -> Unit,
    images: List<Bitmap>,
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit,
    onImageUpload: (Uri) -> Unit = {}
) {
    val context = LocalContext.current
    
    // 앨범에서 이미지 선택을 위한 launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(selectedUri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                // 이미지 업로드 호출
                onImageUpload(selectedUri)
                onAddClick()
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "신청 내용",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 이미지 박스
        LazyRow {
            // 📷 카메라 버튼
            item {
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF0F0F0))
                        .clickable { 
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera),
                            contentDescription = "카메라 아이콘",
                            modifier = Modifier
                                .size(width = 21.dp, height = 18.9.dp),
                            tint = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp)) // 아이콘과 텍스트 간격 조절

                        Text(
                            text = "${images.size}/10",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Gray,
                                fontSize = 8.sp
                            ),
                        )

                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            // 업로드된 이미지들
            itemsIndexed(images) { index, bitmap ->
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF0F0F0))
                        .clickable { 
                            imagePickerLauncher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "첨부 이미지",
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(10.dp) // 바깥 원 크기 = 10x10px = 약 20.dp
                            .background(Color.White, shape = CircleShape)
                            .clickable { onRemoveClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(8.dp) // 아이콘 크기 = 4x4px = 약 10.dp
                        )
                    }



                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 텍스트 입력 + 글자수 표시를 감싸는 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .width(340.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= 5000) onTextChange(it)
                },
                placeholder = { Text("내용을 입력해주세요") },
                modifier = Modifier
                    .fillMaxSize()
            )

            // 오른쪽 하단 글자수 텍스트
            Text(
                text = "${text.length} / 5000",
                style = TextStyle(
                    fontSize = 8.sp,
                    color = Color(0xFFB0B0B0)
                ),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 15.dp, bottom = 15.dp)
            )

        }

    }
}

@Preview(showBackground = true)
@Composable
fun CommissionImageTextSectionPreview() {
    val dummyImage = remember {
        Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }
    val images = remember { mutableStateListOf<Bitmap>(dummyImage, dummyImage) }
    var text by remember { mutableStateOf("") }

    CommitTheme {
        CommissionImageTextSection(
            text = text,
            onTextChange = { text = it },
            images = images,
            onAddClick = { /* 이미지 추가 로직 */ },
            onRemoveClick = { index -> images.removeAt(index) }
        )
    }
}
