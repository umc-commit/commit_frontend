package com.example.commit.ui.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
fun SearchInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSearchSubmit: () -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
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
                textDecoration = TextDecoration.None
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text,
                autoCorrect = false
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearchSubmit()
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
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (query.isEmpty()) {
                            Text(
                                text = "@작가로 검색이 가능해요.",
                                style = CommitTypography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = Color.Gray
                            )
                        }
                        innerTextField()
                    }

                    if (query.isNotEmpty()) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = "지우기",
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onClearClick() },
                            tint = Color(0xFFA8A8A8)
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

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

@Preview(showBackground = true)
@Composable
private fun PreviewSearchInputBar() {
    var q by remember { mutableStateOf("") }
    SearchInputBar(
        query = q,
        onQueryChange = { q = it },
        onBackClick = {},
        onClearClick = { q = "" },
        onHomeClick = {},
        onSearchSubmit = {}
    )
}
