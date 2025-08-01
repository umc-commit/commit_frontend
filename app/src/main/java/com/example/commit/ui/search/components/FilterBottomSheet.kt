package com.example.commit.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults.outlinedButtonBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    selectedSort: String,
    onSortChange: (String) -> Unit,
    minPrice: String,
    maxPrice: String,
    onMinPriceChange: (String) -> Unit,
    onMaxPriceChange: (String) -> Unit,
    selectedDeadline: String,
    onDeadlineChange: (String) -> Unit,
    onReset: () -> Unit,
    onApply: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 800.dp)
                .padding(vertical = 32.dp, horizontal = 24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "상세필터 아이콘",
                    tint = Color(0xFF333333),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "상세필터",
                    style = CommitTypography.titleMedium,
                    color = Color(0xFF333333)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("정렬", style = CommitTypography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            listOf("최신순", "저가순", "고가순").forEach { label ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSortChange(label) }
                ) {
                    RadioButton(
                        selected = selectedSort == label,
                        onClick = { onSortChange(label) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFF17D5C6),
                            unselectedColor = Color(0xFFE0E0E0)
                        )
                    )
                    Text(
                        text = label,
                        style = CommitTypography.bodyMedium,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("가격", style = CommitTypography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PriceInputField(
                    value = minPrice,
                    onValueChange = onMinPriceChange,
                    placeholder = "최소금액"
                )
                PriceInputField(
                    value = maxPrice,
                    onValueChange = onMaxPriceChange,
                    placeholder = "최대금액"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("마감 기한", style = CommitTypography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            val deadlines = listOf("전체보기", "당일 마감", "7일 이내", "14일 이내", "한달 이내")
            val scrollState = rememberScrollState()

            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                deadlines.forEach { label ->
                    val isSelected = selectedDeadline == label

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = if (isSelected) Color(0xFF17D5C6) else Color.White
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isSelected) Color(0xFF17D5C6) else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onDeadlineChange(label) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            style = CommitTypography.labelSmall,
                            color = if (isSelected) Color.White else Color(0xFF333333)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF333333)
                    ),
                    border = outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE0E0E0))
                    )
                ) {
                    Text("초기화", style = CommitTypography.bodyMedium)
                }

                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4B4B4B),
                        contentColor = Color.White
                    )
                ) {
                    Text("적용하기", style = CommitTypography.bodyMedium)
                }
            }
        }
    }
}
