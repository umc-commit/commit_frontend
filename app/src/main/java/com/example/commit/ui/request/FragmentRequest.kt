package com.example.commit.ui.request

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.data.model.Artist
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.request.components.FilterRow
import com.example.commit.ui.request.components.RequestCard

val notoSansKR = FontFamily(
    Font(R.font.notosanskr_regular, FontWeight.Normal),
    Font(R.font.notosanskr_medium, FontWeight.Medium),
    Font(R.font.notosanskr_semibold, FontWeight.SemiBold),
    Font(R.font.notosanskr_bold, FontWeight.Bold)
)

class FragmentRequest : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("FragmentRequest", "onCreateView 진입")

        return ComposeView(requireContext()).apply {
            setContent {
                Log.d("FragmentRequest", "setContent 실행")

                val sampleRequests = listOf(
                    RequestItem(
                        requestId = 1,
                        status = "IN_PROGRESS",
                        title = "귀여운 타입 커미션",
                        price = 40000,
                        thumbnailImage = "",
                        artist = Artist(10, "키르")
                    ),
                    RequestItem(
                        requestId = 2,
                        status = "ACCEPTED",
                        title = "꼼꼼한 타입 커미션",
                        price = 40000,
                        thumbnailImage = "",
                        artist = Artist(10, "키르")
                    ),
                    RequestItem(
                        requestId = 3,
                        status = "DONE",
                        title = "낙서 타입 커미션",
                        price = 40000,
                        thumbnailImage = "",
                        artist = Artist(10, "키르")
                    ),
                    RequestItem(
                        requestId = 4,
                        status = "DONE",
                        title = "2인 캐릭터 세트",
                        price = 60000,
                        thumbnailImage = "",
                        artist = Artist(11, "리아")
                    )
                )

                Log.d("FragmentRequest", "RequestScreen 진입 전")
                RequestScreen(requests = sampleRequests)
            }
        }
    }
}

@Composable
fun RequestScreen(requests: List<RequestItem>) {
    Log.d("RequestScreen", "컴포저블 시작됨")

    var selectedStatus by remember { mutableStateOf("전체") }

    val filteredRequests = when (selectedStatus) {
        "진행 중" -> requests.filter { it.status == "IN_PROGRESS" || it.status == "ACCEPTED" }
        "작업 완료" -> requests.filter { it.status != "IN_PROGRESS" }
        else -> requests
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8)) // gray1
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
        ) {
            Text(
                text = "신청함",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = notoSansKR,
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
                    RequestCard(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun RequestScreenPreview() {
    val sample = listOf(
        RequestItem(
            requestId = 1,
            status = "IN_PROGRESS",
            title = "낙서 타입 커미션",
            price = 40000,
            thumbnailImage = "",
            artist = Artist(10, "키르")
        ),
        RequestItem(
            requestId = 2,
            status = "DONE",
            title = "완료된 작업",
            price = 50000,
            thumbnailImage = "",
            artist = Artist(11, "리아")
        )
    )
    RequestScreen(requests = sample)
}
