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


class FragmentRequest : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val sampleRequests = listOf(
                    RequestItem(
                        requestId = 1,
                        status = "PENDING",
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
                        status = "IN_PROGRESS",
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
                    ) ,
                    RequestItem(
                        requestId = 5,
                    status = "CANCEL",
                    title = "낙서 타입 커미션",
                    price = 40000,
                    thumbnailImage = "",
                    artist = Artist(10, "키르")
                ),
                RequestItem(
                    requestId = 6,
                    status = "REJECT",
                    title = "낙서 타입 커미션",
                    price = 40000,
                    thumbnailImage = "",
                    artist = Artist(10, "키르")
                )
                )

                RequestScreen(
                    requests = sampleRequests,
                    onCardClick = { item ->
                        val detailFragment = FragmentRequestDetail().apply {
                            arguments = Bundle().apply {
                                putParcelable("requestItem", item)

                                putParcelableArrayList("timeline", arrayListOf(
                                    TimelineItem("COMPLETED", "작업이 완료 되었어요.", "25.05.03 10:57"),
                                    TimelineItem("CONFIRMED", "작업물을 확인했어요.", "25.05.03 10:44"),
                                    TimelineItem("DELIVERED", "추가금 4,000P를 결제했어요.", "25.05.01 13:11"),
                                    TimelineItem("DELIVERED", "작업물이 전달되었어요.", "25.05.01 12:30"),
                                    TimelineItem("STARTED", "작가가 작업을 시작했어요.", "25.04.25 17:00"),
                                    TimelineItem("ACCEPTED", "작가가 작업을 수락했어요.", "25.04.25 16:00")
                                ))

                                putParcelable("paymentInfo", PaymentInfo(
                                    paidAt = "2025-06-30 20:05",
                                    basePrice = 40000,
                                    additionalPrice = 0,
                                    totalPrice = 40000,
                                    paymentMethod = "kakaopay"
                                ))

                                putParcelableArrayList("formSchema", arrayListOf(
                                    FormItem("radio", "당일마감 옵션", listOf(OptionItem("O (+10000P)"))),
                                    FormItem("radio", "신청 부위", listOf(OptionItem("전신"))),
                                    FormItem("checkbox", "프로필 공지사항 확인해주세요 !", listOf(OptionItem("확인했습니다."))),
                                    FormItem("textarea", "신청 내용")
                                ))

                                putSerializable("formAnswer", hashMapOf<String, Any>(
                                    "당일마감 옵션" to "yes",
                                    "신청 부위" to "전신",
                                    "프로필 공지사항 확인해주세요 !" to listOf("확인했습니다."),
                                    "신청 내용" to "귀엽게 그려주세요~"
                                ))
                            }
                        }

                        val transaction = (requireActivity() as AppCompatActivity)
                            .supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.Nav_Frame, detailFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
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
        "진행 중" -> requests.filter { it.status == "IN_PROGRESS" || it.status == "ACCEPTED" }
        "거래 완료" -> requests.filter { it.status == "DONE" }
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