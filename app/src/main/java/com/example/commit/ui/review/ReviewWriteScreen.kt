package com.example.commit.ui.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.commit.R

@Composable
fun ReviewWriteScreen(
    images: List<Any> = emptyList(),
    onAddClick: () -> Unit = {}
) {
    var selectedRating by remember { mutableStateOf(0) }
    var content by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 28.dp, vertical = 24.dp)
                .padding(bottom = 80.dp)
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "후기 작성",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF222222),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bg_thumbnail_rounded),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "낙서 타입 커미션",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "작업기간 : 23시간",
                        fontSize = 10.sp,
                        color = Color(0xFF999999)
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.ic_see_post),
                    contentDescription = null,
                    modifier = Modifier
                        .size(width = 48.dp, height = 25.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFB0B0B0), RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "별점",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                repeat(5) { index ->
                    Icon(
                        painter = painterResource(id = if (index < selectedRating) R.drawable.ic_star_on else R.drawable.ic_star_off),
                        contentDescription = null,
                        modifier = Modifier
                            .size(22.dp)
                            .padding(end = 4.dp)
                            .clickable { selectedRating = index + 1 },
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "내용 작성",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF0F0F0))
                            .clickable { onAddClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_camera),
                                contentDescription = "카메라 아이콘",
                                modifier = Modifier.size(width = 21.dp, height = 18.9.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${images.size}/10",
                                fontSize = 8.sp,
                                color = Color.Gray,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = content,
                        onValueChange = {
                            content = it
                            showError = it.length in 1..9
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(148.dp),
                        placeholder = {
                            Text(
                                text = "후기를 작성해주세요.",
                                fontSize = 14.sp,
                                color = Color(0xFFB0B0B0)
                            )
                        },
                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                        maxLines = 6,
                        isError = showError
                    )
                    if (showError) {
                        Text(
                            text = "최소 10자 이상 작성해주세요",
                            fontSize = 12.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text(
                        text = "${content.length}/1000",
                        fontSize = 12.sp,
                        color = Color(0xFF999999),
                        modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                    )
                }
            }
        }

        Button(
            onClick = {},
            enabled = selectedRating > 0 && content.length >= 10,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 28.dp, vertical = 12.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedRating > 0 && content.length >= 10) Color(0xFF4D4D4D) else Color(0xFFE0E0E0)
            )
        ) {
            Text(
                text = "작성 완료",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewWriteScreenPreview() {
    ReviewWriteScreen()
}
