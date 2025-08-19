package com.example.commit.ui.FormCheck

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun FormCheckSection(
    item: ChatItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 1. 커미션 작가 영역
        Text(
            text = "커미션 작가",
            style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!item.profileImageUrl.isNullOrBlank()) {
                coil.compose.AsyncImage(
                    model = item.profileImageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            } else {
                // 리소스/기본 아이콘
                Image(
                    painter = painterResource(id = item.profileImageRes),
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp).clip(CircleShape)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = item.name,
                style = CommitTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .height(22.dp)
                    .width(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF4D4D4D)
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = SolidColor(Color(0xFF4D4D4D))
                ),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                onClick = onBackClick,
            ) {
                Text(
                    text = "채팅하기",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }

        // 2. 폼 내용 영역
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "작성한 폼 내용",
            style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        formSchema.forEachIndexed { index, schemaItem ->
            val answer = formAnswer[schemaItem.label]
            val answerText = when (answer) {
                is List<*> -> answer.mapNotNull { it?.toString() }.joinToString(", ")
                is String -> answer
                else -> " "
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "${index + 1}. ${schemaItem.label}",
                        style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = answerText.toString(),
                        style = CommitTypography.labelLarge,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // 3. 신청 내용 + 이미지 영역
        val note = formAnswer["신청 내용"] as? String
        val imageUrl = formAnswer["이미지"] as? String

        if (!note.isNullOrBlank() || !imageUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "신청 내용",
                style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp)
            ) {
                if (!note.isNullOrBlank()) {
                    Text(
                        text = note,
                        style = CommitTypography.labelLarge,
                        color = Color.Black
                    )
                }

                if (!imageUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "첨부 이미지",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
