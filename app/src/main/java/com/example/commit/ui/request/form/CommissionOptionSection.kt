package com.example.commit.ui.request.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun CommissionOptionSection(
    index: Int,
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val primaryColor = Color(0xFF17D5C6)

    CommissionSectionWrapper(
        index = index,
        title = title,
        isRequired = true
    ) {
        if (options.size == 1) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == options[0],
                    onClick = { onOptionSelected(options[0]) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = primaryColor,
                        unselectedColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = options[0],
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            // ✅ 라디오버튼 (2개 이상 옵션일 경우 - 가로 정렬)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedOption == option,
                            onClick = { onOptionSelected(option) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = primaryColor,
                                unselectedColor = Color.LightGray
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommissionOptionSection() {
    var selected by remember { mutableStateOf("") }

    CommitTheme {
        Column(modifier = Modifier.width(400.dp).padding(16.dp)) {
            CommissionOptionSection(
                index = 1,
                title = "당일마감 옵션",
                options = listOf("O (+10000P)", "X"),
                selectedOption = selected,
                onOptionSelected = { selected = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            CommissionOptionSection(
                index = 3,
                title = "저희 팀 코밋 예쁘게 봐주세요!",
                options = listOf("확인했습니다."),
                selectedOption = selected,
                onOptionSelected = { selected = it }
            )
        }
    }
}
