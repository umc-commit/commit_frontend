package com.example.commit.ui.chat

import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // 또는 imageVector용 import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTheme

@Composable
fun ChatSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .width(344.dp)
            .height(34.dp)
            .border(
                width = 1.dp,
                color = Color(0xFFB0B0B0),
                shape = RoundedCornerShape(11.dp)
            )
            .padding(start = 11.dp, end = 11.dp), // 왼오 패딩
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search_gray),
            contentDescription = "검색 아이콘",
            modifier = Modifier.size(10.dp),
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(10.dp))

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 10.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "내용 검색하기",
                            fontSize = 10.sp,
                            color = Color(0xFFB0B0B0),
                            style = MaterialTheme.typography.labelSmall.copy(
                                    )
                        )

                    }
                    innerTextField()
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatSearchBarPreview() {
    var searchText by remember { mutableStateOf("") }

    CommitTheme {
        ChatSearchBar(
            query = searchText,
            onQueryChange = { searchText = it }
        )
    }
}

