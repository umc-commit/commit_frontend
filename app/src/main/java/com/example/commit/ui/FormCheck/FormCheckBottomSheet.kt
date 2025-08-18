package com.example.commit.ui.FormCheck

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.viewmodel.CommissionFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormCheckBottomSheet(
    chatItem: ChatItem,
    requestItem: RequestItem,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            val formVM: CommissionFormViewModel = viewModel()
            // FormCheckScreen 내용
            FormCheckScreen(
                chatItem = chatItem,
                requestItem = requestItem,
                formSchema = formSchema,
                formAnswer = formAnswer,
                onBackClick = onDismiss,
                viewModel = formVM
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
} 