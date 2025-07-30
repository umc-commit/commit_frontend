package com.example.commit.ui.search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun SearchInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //뒤로가기
        Icon(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트 필드
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = {
                Text(
                    text = "작가 이름은 @작가로 검색이 가능해요.",
                    style = CommitTypography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = Color.Gray
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "지우기",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onClearClick() },
                        tint = Color(0xFFA8A8A8)
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F3F3),
                unfocusedContainerColor = Color(0xFFF3F3F3),
                disabledContainerColor = Color(0xFFF3F3F3),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = CommitTypography.bodyMedium.copy(color = Color.Black),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 홈 아이콘
        Icon(
            painter = painterResource(id = R.drawable.ic_search_home),
            contentDescription = "홈",
            modifier = Modifier
                .size(20.dp)
                .clickable { onHomeClick() },
            tint = Color(0xFF4D4D4D)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SearchInputBarPreview() {
    var query by remember { mutableStateOf("") }

    SearchInputBar(
        query = query,
        onQueryChange = { query = it },
        onBackClick = {},
        onClearClick = { query = "" },
        onHomeClick = {}
    )
}
