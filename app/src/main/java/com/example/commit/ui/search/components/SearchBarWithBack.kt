package com.example.commit.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SearchBarWithBack(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "원하는 커미션을 입력해보세요.",
                    style = CommitTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Gray
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "검색",
                    modifier = Modifier.clickable { onSearchClick() },
                    tint = Color(0xFFB0B0B0)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F3F3),
                unfocusedContainerColor = Color(0xFFF3F3F3),
                disabledContainerColor = Color(0xFFF3F3F3),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SearchBarWithBackPreview() {
    var query by remember { mutableStateOf("") }

    SearchBarWithBack(
        query = query,
        onQueryChange = { query = it },
        onSearchClick = {},
        onBackClick = {}
    )
}
