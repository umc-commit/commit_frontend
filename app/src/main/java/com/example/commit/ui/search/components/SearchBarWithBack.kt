package com.example.commit.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography

@Composable
fun SearchBarWithBack(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current

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

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            maxLines = 1,
            textStyle = CommitTypography.bodyMedium.copy(
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textDecoration = TextDecoration.None
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text,
                autoCorrect = false
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchClick()
                    keyboard?.hide()
                }
            ),
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(0xFFF3F3F3),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 텍스트 영역
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "원하는 커미션을 입력해보세요.",
                                style = CommitTypography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }

                    // 검색 아이콘
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "검색",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                onSearchClick()
                                keyboard?.hide()
                            },
                        tint = Color(0xFFB0B0B0)
                    )
                }
            }
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
