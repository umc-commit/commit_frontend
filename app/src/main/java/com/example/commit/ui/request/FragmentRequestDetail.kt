package com.example.commit.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.connection.dto.*
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.request.components.RequestDetailItem
import com.example.commit.ui.request.components.RequestDetailSectionList
import com.example.commit.viewmodel.RequestDetailViewModel
import android.util.Log
import androidx.compose.runtime.getValue

class FragmentRequestDetail : Fragment() {

    private val viewModel: RequestDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val requestId = arguments?.getInt("requestId") ?: -1
        Log.d("FragmentRequestDetail", "받은 requestId: $requestId")
        if (requestId == -1) {
            Log.e("FragmentRequestDetail", "requestId 없음!")
            return ComposeView(requireContext()).apply {
                setContent {
                    Text("유효하지 않은 요청 ID입니다.", style = CommitTypography.bodyMedium)
                }
            }
        }

        viewModel.loadRequestDetail(requireContext(), requestId)

        return ComposeView(requireContext()).apply {
            setContent {
                val detail by viewModel.requestDetail.collectAsState()
                val error by viewModel.errorMessage.collectAsState()

                when {
                    detail != null -> {
                        val request = detail!!.request
                        val commission = detail!!.commission ?: CommissionItem(
                            id = -1,
                            title = "알 수 없음",
                            thumbnailImageUrl = "",
                            artist = Artist(id = -1, nickname = "알 수 없음")
                        )
                        val timeline = detail!!.timeline ?: emptyList()
                        val payment = detail!!.payment ?: PaymentInfo(
                            minPrice = 0,
                            additionalPrice = 0,
                            totalPrice = 0,
                            paidAt = ""
                        )
                        val formSchema = detail!!.formData ?: emptyList()

                        // JsonElement → String 변환 처리
                        val formAnswer = formSchema.associate { item ->
                            val valueStr = when {
                                item.value.isJsonPrimitive -> item.value.asString
                                item.value.isJsonArray -> item.value.asJsonArray.joinToString(", ") { it.asString }
                                item.value.isJsonObject -> item.value.asJsonObject.toString()
                                else -> item.value.toString()
                            }
                            item.label to valueStr
                        }

                        RequestDetailScreen(
                            item = request,
                            commission = commission,
                            timeline = timeline,
                            paymentInfo = payment,
                            formSchema = formSchema,
                            formAnswer = formAnswer,
                            onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() }
                        )
                    }

                    error != null -> {
                        Text("오류 발생: $error", style = CommitTypography.bodyMedium)
                    }

                    else -> {
                        Text("불러오는 중입니다...", style = CommitTypography.bodyMedium)
                    }
                }
            }
        }
    }
}


@Composable
fun RequestDetailScreen(
    item: RequestItem,
    commission: CommissionItem,
    timeline: List<TimelineItem>,
    paymentInfo: PaymentInfo,
    formSchema: List<FormItem>,
    formAnswer: Map<String, String>,
    onBackClick: () -> Unit,
    onFormAnswerClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // 바텀바 숨기기
    LaunchedEffect(Unit) {
        (context as? MainActivity)?.showBottomNav(false)
    }

    // 뒤로 가기 시 바텀바 다시 보이기
    DisposableEffect(Unit) {
        onDispose {
            (context as? MainActivity)?.showBottomNav(true)
        }
    }

    val status = item.status.trim()
    val isCancel = status == "CANCELED"
    val isReject = status == "REJECTED"
    val isPending= status == "PENDING"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8))
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_left_vector),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = "상세정보",
                fontSize = 18.sp,
                style = CommitTypography.titleMedium,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            RequestDetailItem(
                item = item,
                commission = commission,
                totalPrice = paymentInfo.totalPrice,
            )

            // 취소 또는 거절 또는 수락 대기 상태가 아니면 세부 항목 출력
            if (!isCancel && !isReject &&!isPending) {
                Spacer(modifier = Modifier.height(16.dp))
                RequestDetailSectionList(
                    timeline = timeline,
                    paymentInfo = paymentInfo,
                    formSchema = formSchema,
                    formAnswer = formAnswer
                )
            }
        }
    }
}
