package com.example.commit.ui.chatroom

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ChatroomTopBar(title: String) {
    TopAppBar(
        title = {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            IconButton(onClick = { /* 뒤로가기 */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { /* 검색 */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
            IconButton(onClick = { /* 메뉴 */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
        }
    )
}
