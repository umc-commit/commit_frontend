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
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.data.model.*

@Composable
fun RequestDetailSectionList(
    timeline: List<TimelineItem>,
    paymentInfo: PaymentInfo,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>
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
            timeline.forEach { item ->
                Text("${item.changedAt}: ${item.label}", fontSize = 14.sp, modifier = Modifier.padding(4.dp))
            }
        }

        ExpandableItem(
            title = "결제정보",
            expanded = isPaymentExpanded,
            onToggle = { isPaymentExpanded = !isPaymentExpanded }
        ) {
            Text("결제일: ${paymentInfo.paidAt}")
            Text("결제수단: ${paymentInfo.paymentMethod}")
            Text("기본 금액: ${paymentInfo.basePrice}P")
            Text("추가 금액: ${paymentInfo.additionalPrice}P")
            Text("총 결제 금액: ${paymentInfo.totalPrice}P")
        }

        ExpandableItem(
            title = "신청서 보기",
            expanded = isFormExpanded,
            onToggle = { isFormExpanded = !isFormExpanded }
        ) {
            formSchema.forEach { item ->
                val answer = formAnswer[item.label]
                val answerText = when (answer) {
                    is List<*> -> answer.joinToString(", ")
                    is String -> answer
                    else -> "응답 없음"
                }
                Text("${item.label}: $answerText", fontSize = 14.sp, modifier = Modifier.padding(4.dp))
            }
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            if (expanded) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_up_vector),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.ic_under_vector),
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        if (expanded) {
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
