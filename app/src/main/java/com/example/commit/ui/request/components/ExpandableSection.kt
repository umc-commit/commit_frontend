package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.connection.dto.*


@Composable
fun RequestDetailSectionList(
    timeline: List<TimelineItem>,
    paymentInfo: PaymentInfo,
    formSchema: List<FormItem>,
    formAnswer: Map<String, String>
) {
    var isTimelineExpanded by remember { mutableStateOf(false) }
    var isPaymentExpanded by remember { mutableStateOf(false) }
    var isFormExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(8.dp)
    ) {
        ExpandableItem(
            title = "타임라인",
            expanded = isTimelineExpanded,
            onToggle = { isTimelineExpanded = !isTimelineExpanded }
        ) {
            TimelineSection(events = timeline.map { it.toEvent() })
        }

        ExpandableItem(
            title = "결제정보",
            expanded = isPaymentExpanded,
            onToggle = { isPaymentExpanded = !isPaymentExpanded }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${paymentInfo.paidAt} 결제완료",
                    style = CommitTypography.labelSmall.copy(color = Color.Gray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "기본 금액",
                        style = CommitTypography.bodyMedium.copy(color = Color.Gray)
                    )
                    Text(
                         text = "${"%,d".format(paymentInfo.minPrice)}P",
                        style = CommitTypography.bodyMedium.copy(color = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "추가 금액",
                        style = CommitTypography.bodyMedium.copy(color = Color.Gray)
                    )
                    Text(
                        text = "${"%,d".format(paymentInfo.additionalPrice)}P",
                        style = CommitTypography.bodyMedium.copy(color = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "총 결제 금액",
                        style = CommitTypography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${"%,d".format(paymentInfo.totalPrice)}P",
                        style = CommitTypography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        ExpandableItem(
            title = "신청서 보기",
            expanded = isFormExpanded,
            onToggle = { isFormExpanded = !isFormExpanded }
        ) {
          /*  FormAnswerSection(
                formSchema = formSchema,
                formAnswer = formAnswer
            )*/
        }
    }
}

@Composable
fun ExpandableItem(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = CommitTypography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C1C1C)
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(
                    id = if (expanded) R.drawable.ic_up_vector else R.drawable.ic_under_vector
                ),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
