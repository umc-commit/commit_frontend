package com.example.commit.ui.request.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.Theme.CommitTheme
import android.util.Log
import androidx.compose.foundation.background

@Composable
fun CommissionOptionSection(
    index: Int,
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val primaryColor = Color(0xFF17D5C6)
    
    Log.d("FormDebug", "CommissionOptionSection 렌더링 - index: $index, title: $title, options: $options, selectedOption: $selectedOption")
    
    // selectedOption이 value인지 label인지 구분해서 비교하는 헬퍼 함수
    fun isOptionSelected(selectedValue: String, optionLabel: String): Boolean {
        return selectedValue == optionLabel
    }

    CommissionSectionWrapper(
        index = index,
        title = title,
        isRequired = true
    ) {
        if (options.size == 1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
                    .background(Color.White)
            ) {
                RadioButton(
                    selected = isOptionSelected(selectedOption, options[0]),
                    onClick = { 
                        Log.d("FormDebug", "라디오 버튼 클릭됨 - $title: ${options[0]}")
                        onOptionSelected(options[0]) 
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = primaryColor,
                        unselectedColor = Color.LightGray
                    ),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = options[0],
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Default
                    )
                )
            }
        } else {
            // 라디오버튼 (2개 이상 옵션일 경우 - 가로 정렬)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isOptionSelected(selectedOption, option),
                            onClick = { 
                                Log.d("FormDebug", "라디오 버튼 클릭됨 - $title: $option")
                                onOptionSelected(option) 
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = primaryColor,
                                unselectedColor = Color.LightGray
                            ),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Default
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
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
