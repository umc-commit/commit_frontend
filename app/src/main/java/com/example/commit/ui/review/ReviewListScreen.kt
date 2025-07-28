package com.example.commit.ui.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.data.model.entities.Review
import com.example.commit.ui.review.ReviewItem

@Composable
fun ReviewListScreen(
    reviews: List<Review>,
    onBackClick: () -> Unit,
    onEditClick: (Review) -> Unit,
    onDeleteClick: (Review) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedReview by remember { mutableStateOf<Review?>(null) }

    Column(modifier = Modifier.fillMaxSize()
        .background(Color.White)) {

        // TopBar
        ReviewTopBar(onBackClick = onBackClick)

        Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(reviews) { review ->
                ReviewItem(
                    review = review,
                    onEditClick = { onEditClick(review) },
                    onDeleteClick = {
                        selectedReview = review
                        showDialog = true
                    }
                )
            }
        }

        // 삭제 팝업 다이얼로그
        if (showDialog && selectedReview != null) {
            ReviewDeleteDialog(
                onDelete = {
                    onDeleteClick(selectedReview!!)
                    selectedReview = null
                    showDialog = false
                },
                onDismiss = {
                    selectedReview = null
                    showDialog = false
                }
            )
        }
    }
}

