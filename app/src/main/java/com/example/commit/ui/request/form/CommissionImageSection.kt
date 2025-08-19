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
import android.util.Log

@Composable
fun CommissionImageSection(
    index: Int,
    images: List<Bitmap>,
    onAddClick: () -> Unit,
    onRemoveClick: (Int) -> Unit,
    onImageUpload: (Uri) -> Unit = {},
    onImageAdded: (Bitmap) -> Unit = {}
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
                
                // 이미지를 UI에 추가
                onImageAdded(bitmap)
                
                // 이미지 업로드 호출
                onImageUpload(selectedUri)
                
                // 기존 onAddClick 호출
                onAddClick()
                
                Log.d("ImageUpload", "이미지 선택됨: $selectedUri")
            } catch (e: Exception) {
                Log.e("ImageUpload", "이미지 처리 오류: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .background(Color.White)
    ) {
        // 번호와 제목 표시
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${index}. 신청 내용",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Default
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

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

                        Spacer(modifier = Modifier.height(4.dp))

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
                            .size(10.dp)
                            .background(Color.White, shape = CircleShape)
                            .clickable { onRemoveClick(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommissionImageSectionPreview() {
    val dummyImage = remember {
        Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    }
    val images = remember { mutableStateListOf<Bitmap>(dummyImage, dummyImage) }

    CommitTheme {
        CommissionImageSection(
            index = 2,
            images = images,
            onAddClick = { /* 이미지 추가 로직 */ },
            onRemoveClick = { index -> images.removeAt(index) }
        )
    }
}
