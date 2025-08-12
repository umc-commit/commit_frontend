package com.example.commit.ui.request.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.connection.dto.TimelineItem
import com.example.commit.ui.Theme.CommitTypography
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class TimelineEvent(
    val iconRes: Int,
    val description: String,
    val date: String
)

@Composable
fun TimelineSection(events: List<TimelineEvent>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        events.forEachIndexed { index, event ->
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.width(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = event.iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    if (index != events.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(48.dp)
                                .background(Color(0xFFEDEDED))
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.padding(top = 2.dp)) {
                    Text(
                        text = event.description,
                        style = CommitTypography.bodyMedium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.date, // "yy.MM.dd HH:mm" (KST)
                        style = CommitTypography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

/** 여러 ISO 포맷을 SDF로 파싱해서 KST 'yy.MM.dd HH:mm'로 변환 */
private fun parseIsoWithSdfToKst(iso: String?): String {
    if (iso.isNullOrBlank()) return "-"
    val patterns = arrayOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ssX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )
    val out = SimpleDateFormat("yy.MM.dd HH:mm", Locale.KOREA).apply {
        timeZone = TimeZone.getTimeZone("Asia/Seoul")
    }
    for (p in patterns) {
        try {
            val sdf = SimpleDateFormat(p, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(iso)
            if (date != null) return out.format(date)
        } catch (_: ParseException) {
        }
    }
    return "-"
}

// API DTO(TimelineItem) → UI 모델 변환
fun TimelineItem.toEvent(): TimelineEvent {
    val clean = status.trim()
    val iconRes = when (clean) {
        "COMPLETED" -> R.drawable.timeline_complete
        "SUBMITTED" -> R.drawable.timeline_confirm
        "IN_PROGRESS" -> R.drawable.timeline_delivery
        "PAID" -> R.drawable.timeline_start
        "APPROVED" -> R.drawable.timeline_accept
        else -> R.drawable.timeline_complete
    }
    return TimelineEvent(
        iconRes = iconRes,
        description = getStatusLabel(clean),
        date = parseIsoWithSdfToKst(this.timestamp)
    )
}

// 상태값 → 설명 문구
fun getStatusLabel(status: String): String {
    return when (status.trim()) {
        "COMPLETED" -> "거래가 완료됐어요."
        "SUBMITTED" -> "작업물이 전달되었어요."
        "IN_PROGRESS" -> "작업을 시작했어요."
        "PAID" -> "커미션 결제가 완료됐어요."
        "APPROVED" -> "작업을 수락했어요."
        else -> "알 수 없는 상태입니다."
    }
}
