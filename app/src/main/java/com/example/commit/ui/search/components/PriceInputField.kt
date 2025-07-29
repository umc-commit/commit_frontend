package com.example.commit.ui.search.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun PriceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Row(
        modifier = Modifier
            .width(169.dp)
            .height(31.dp)
            .border(
                width = 1.5.dp,
                color = Color(0xFFE0E0E0), // 연한 회색
                shape = androidx.compose.foundation.shape.RoundedCornerShape(15.5.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = CommitTypography.labelSmall.copy(color = Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(end = 10.dp),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = CommitTypography.labelSmall.copy(color = Color(0xFFBDBDBD)) // placeholder 색상
                    )
                }
                innerTextField()
            }
        )

        Text("P", style = CommitTypography.labelSmall, color = Color(0xFF333333))
    }
}

@Preview(showBackground = true)
@Composable
fun PriceInputFieldPreview() {
    var value by remember { mutableStateOf("") }
    PriceInputField(
        value = value,
        onValueChange = { value = it },
        placeholder = "최대금액"
    )
}
