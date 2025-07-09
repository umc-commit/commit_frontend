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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.data.model.*
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
        return ComposeView(requireContext()).apply {
            setContent {
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

                RequestScreen(
                    requests = sampleRequests,
                    onCardClick = { item ->
                        val detailFragment = FragmentRequestDetail().apply {
                            arguments = Bundle().apply {
                                putParcelable("requestItem", item)

                                // mock 데이터 전달
                                putParcelableArrayList("timeline", arrayListOf(
                                    TimelineItem(
                                        status = "IN_PROGRESS",
                                        label = "작가가 작업을 시작했어요.",
                                        changedAt = "2025-06-30 20:12"
                                    ),
                                    TimelineItem(
                                        status = "SUBMITTED",
                                        label = "작가가 작업을 완료했어요.",
                                        changedAt = "2025-06-30 20:40"
                                    )
                                ))

                                putParcelable("paymentInfo", PaymentInfo(
                                    paidAt = "2025-06-30 20:05",
                                    basePrice = 40000,
                                    additionalPrice = 0,
                                    totalPrice = 40000,
                                    paymentMethod = "kakaopay"
                                ))

                                putParcelableArrayList("formSchema", arrayListOf(
                                    FormItem(
                                        type = "radio",
                                        label = "당일마감 옵션",
                                        options = listOf(OptionItem("O (+10000P)"))
                                    ),
                                    FormItem(
                                        type = "radio",
                                        label = "신청 부위",
                                        options = listOf(OptionItem("전신"))
                                    ),
                                    FormItem(
                                        type = "checkbox",
                                        label = "프로필 공지사항 확인해주세요 !",
                                        options = listOf(OptionItem("확인했습니다."))
                                    ),
                                    FormItem(
                                        type = "textarea",
                                        label = "신청 내용"
                                    )
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
        "작업 완료" -> requests.filter { it.status == "DONE" }
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
                    RequestCard(item = item, onClick = { onCardClick(item) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
