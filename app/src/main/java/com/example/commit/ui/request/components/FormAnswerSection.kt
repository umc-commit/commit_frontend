package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.commit.R
import com.example.commit.data.model.FormItem
import com.example.commit.ui.request.notoSansKR

val notoSansKR = FontFamily(
    Font(R.font.notosanskr_regular, FontWeight.Normal),
    Font(R.font.notosanskr_medium, FontWeight.Medium),
    Font(R.font.notosanskr_semibold, FontWeight.SemiBold),
    Font(R.font.notosanskr_bold, FontWeight.Bold)
)

@Composable
fun FormAnswerSection(
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "신청 폼",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = notoSansKR,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        formSchema.forEachIndexed { index, item ->
            val answer = formAnswer[item.label]
            val answerText = when (answer) {
                is List<*> -> answer.joinToString(", ")
                is String -> answer
                else -> "응답 없음"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "${index + 1}. ${item.label}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = notoSansKR,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = answerText.toString(),
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        fontFamily = notoSansKR
                    )
                }
            }
        }

        // 신청 내용 + 이미지
        val note = formAnswer["신청 내용"] as? String
        val imageUrl = formAnswer["이미지"] as? String

        if (!note.isNullOrBlank() || !imageUrl.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "신청 내용",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = notoSansKR,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                if (!note.isNullOrBlank()) {
                    Text(
                        text = note,
                        fontSize = 13.sp,
                        color = Color.Black,
                        fontFamily = notoSansKR
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
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFormAnswerSection() {
    val dummyFormSchema = listOf(
        FormItem(type = "radio", label = "당일마감 옵션", options = listOf()),
        FormItem(type = "radio", label = "신청 부위", options = listOf()),
        FormItem(type = "checkbox", label = "프로필 공사항 확인해주세요!", options = listOf())
    )

    val dummyFormAnswer = mapOf(
        "당일마감 옵션" to "O",
        "신청 부위" to "전신",
        "프로필 공사항 확인해주세요!" to listOf("알겠습니다"),
        "신청 내용" to "귀엽게 그려주세요!",
        "이미지" to "https://i.namu.wiki/i/eo5nbfHMQNRzJv3_f-qY6sDXo3aZqj8DPTk09XIEAK9Ugh6J53ZYXxhFjZUBuPj_aqFaz6XWZ5eUHRoWKLHfRA.webp"
    )

    FormAnswerSection(formSchema = dummyFormSchema, formAnswer = dummyFormAnswer)
}
