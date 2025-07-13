package com.example.commit.ui.request.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun CommissionOptionSection(
    index: Int,
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    CommissionSectionWrapper(
        index = index,
        title = title,
        isRequired = true
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "확인했습니다.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCommissionCheckboxSection() {
    var checked by remember { mutableStateOf(false) }

    CommitTheme {
        CommissionOptionSection(
            index = 3,
            title = "저희 팀 코밋 예쁘게 봐주세요!",
            isChecked = checked,
            onCheckedChange = { checked = it }
        )
    }
}
