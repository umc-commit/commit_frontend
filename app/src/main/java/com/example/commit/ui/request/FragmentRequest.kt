package com.example.commit.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.data.model.*
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.request.components.FilterRow
import com.example.commit.ui.request.components.RequestCard
import androidx.compose.ui.unit.sp
import com.example.commit.viewmodel.RequestViewModel
import androidx.fragment.app.viewModels
import com.example.commit.connection.dto.RequestItem

class FragmentRequest : Fragment() {

    private val requestViewModel: RequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val requestList by requestViewModel.requestList.collectAsState(initial = emptyList())

                LaunchedEffect(Unit) {
                    requestViewModel.loadRequests(requireContext())
                }

                RequestScreen(
                    requests = requestList,
                    onCardClick = { item ->
                        val detailFragment = FragmentRequestDetail().apply {
                            arguments = Bundle().apply {
                                putInt("requestId", item.requestId)
                            }
                        }

                        (requireActivity() as AppCompatActivity).supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.Nav_Frame, detailFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                )
            }
        }
    }
}


@Composable
fun RequestScreen(
requests: List<RequestItem>,
    onCardClick: (RequestItem) -> Unit
) {
    var selectedStatus by remember { mutableStateOf("전체") }

    val filteredRequests = when (selectedStatus) {
        "진행 중" -> requests.filter { it.status == "IN_PROGRESS" ||it.status == "APPROVED" }
        "작업 완료" -> requests.filter { it.status == "COMPLETED" ||it.status == "SUBMITTED"}
        else -> requests
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
        ) {
            Text(
                text = "신청함",
                style = CommitTypography.headlineSmall.copy(fontSize = 18.sp),
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        FilterRow(selectedStatus) { selectedStatus = it }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(filteredRequests) { item ->
                Column {
                   RequestCard(item = item, onClick = { onCardClick(item) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}