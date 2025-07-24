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
import com.example.commit.data.model.TimelineEvent
import com.example.commit.data.model.TimelineItem
import com.example.commit.ui.Theme.CommitTypography

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

// 아이콘 리소스 매핑용 확장 함수
fun TimelineItem.toEvent(): TimelineEvent {
    val iconRes = when (status) {
        "COMPLETED" -> R.drawable.timeline_complete
        "CONFIRMED" -> R.drawable.timeline_confirm
        "DELIVERED" -> R.drawable.timeline_delivery
        "STARTED" -> R.drawable.timeline_start
        "ACCEPTED" -> R.drawable.timeline_accept
        else -> R.drawable.timeline_complete
    }
    return TimelineEvent(
        iconRes = iconRes,
        description = this.label,
        date = this.changedAt
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTimelineSection() {
    val dummyItems = listOf(
        TimelineItem("COMPLETED", "작업이 완료 되었어요.", "25.05.03 10:57"),
        TimelineItem("CONFIRMED", "작업물을 확인했어요.", "25.05.03 10:44"),
        TimelineItem("DELIVERED", "추가금 4,000P를 결제했어요.", "25.05.01 13:11"),
        TimelineItem("DELIVERED", "작업물이 전달되었어요.", "25.05.01 12:30"),
        TimelineItem("STARTED", "작가가 작업을 시작했어요.", "25.04.25 17:00"),
        TimelineItem("ACCEPTED", "작가가 작업을 수락했어요.", "25.04.25 16:00")
    )

    val dummyEvents = dummyItems.map { it.toEvent() }

    TimelineSection(events = dummyEvents)
}
