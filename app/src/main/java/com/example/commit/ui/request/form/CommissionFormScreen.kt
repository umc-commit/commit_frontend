package com.example.commit.ui.request.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.painterResource
import com.example.commit.R
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.ui.request.notoSansKR

@Composable
fun CommissionFormScreen() {
    var selectedDeadlineOption by remember { mutableStateOf("") }
    var selectedCharacter by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("커미션 신청", style = MaterialTheme.typography.headlineSmall)

        // 1. 당일마감 옵션
        Text("1. 당일마감 옵션 *", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedDeadlineOption == "yes",
                onClick = { selectedDeadlineOption = "yes" }
            )
            Text("+1000P", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = selectedDeadlineOption == "no",
                onClick = { selectedDeadlineOption = "no" }
            )
            Text("X", style = MaterialTheme.typography.bodyMedium)
        }

        // 2. 신청 캐릭터
        Text("2. 신청 캐릭터 *", style = MaterialTheme.typography.bodyMedium)
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("고양이", "햄스터", "캐리커쳐", "랜덤").forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedCharacter == option,
                        onClick = { selectedCharacter = option }
                    )
                    Text(option, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // 3. 확인 체크박스
        Text("3. 저희 팀 코믹 예쁘게 봐주세요! *", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            Text("확인했습니다.", style = MaterialTheme.typography.bodyMedium)
        }

        // 4. 신청 내용
        Text("4. 신청 내용", style = MaterialTheme.typography.bodyMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "사진 추가",
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )
            Text("0/10", style = MaterialTheme.typography.labelSmall)
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                if (it.length <= 500) inputText = it
            },
            label = { Text("신청자 텍스트", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            maxLines = 10
        )

        Text("${'$'}{inputText.length}/500", style = MaterialTheme.typography.labelSmall)

        Button(
            onClick = { /* 신청 로직 */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = selectedDeadlineOption.isNotEmpty()
                    && selectedCharacter.isNotEmpty()
                    && isChecked
                    && inputText.isNotBlank()
        ) {
            Text("신청하기", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommissionFormPreview() {
    MaterialTheme {
        CommissionFormScreen()
    }
}
