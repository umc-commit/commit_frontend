package com.example.commit.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun Tag(
    text: String,
    isSelected: Boolean = false,
    fontSize: TextUnit = 5.sp
) {
    val background = if (isSelected) Color(0xFFE0FFFF) else Color(0xFFF3F3F3)
    val textColor = if (isSelected) Color(0xFF17D5C6) else Color(0xFF9E9E9E)
    val borderColor = textColor

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .border(0.7.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = CommitTypography.labelSmall.copy(
                fontSize = fontSize,
                lineHeight = fontSize
            ),
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TagPreview() {
    Row(modifier = Modifier.padding(16.dp)) {
        Tag(text = "그림", isSelected = true)
        Spacer(modifier = Modifier.width(4.dp))
        Tag(text = "#LD")
        Spacer(modifier = Modifier.width(4.dp))
        Tag(text = "#커플")
    }
}
