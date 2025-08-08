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
                        text = event.date,
                        style = CommitTypography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

// API에서 받은 DTO(TimelineItem)를 UI용 TimelineEvent로 변환하는 확장 함수
fun TimelineItem.toEvent(): TimelineEvent {
    val iconRes = when (status) {
        "COMPLETED" -> R.drawable.timeline_complete
        "CONFIRMED" -> R.drawable.timeline_confirm
        "SUBMITTED " -> R.drawable.timeline_delivery
        "STARTED" -> R.drawable.timeline_start
        "ACCEPTED" -> R.drawable.timeline_accept
        "APPROVED" -> R.drawable.timeline_confirm
        else -> R.drawable.timeline_complete
    }
    return TimelineEvent(
        iconRes = iconRes,
        description = getStatusLabel(status),
        date = this.timestamp
    )
}

// 상태값에 따른 설명 텍스트 반환 함수
fun getStatusLabel(status: String): String {
    return when (status) {
        "COMPLETED" -> "작업이 완료 되었어요."
        "CONFIRMED" -> "작업물을 확인했어요."
        "SUBMITTED " -> "작업물이 전달되었어요."
        "STARTED" -> "작가가 작업을 시작했어요."
        "APPROVED" -> "작가가 작업을 수락했어요."
        "APPROVED" -> "결제가 승인되었어요."
        else -> "알 수 없는 상태입니다."
    }
}

