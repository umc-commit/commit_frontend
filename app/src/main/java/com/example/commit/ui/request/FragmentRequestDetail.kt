package com.example.commit.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.data.model.*
import com.example.commit.ui.request.components.RequestDetailItem
import com.example.commit.ui.request.components.RequestDetailSectionList
import com.example.commit.ui.request.notoSansKR
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

class FragmentRequestDetail : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val item = arguments?.getParcelable<RequestItem>("requestItem")
                val timeline = arguments?.getParcelableArrayList<TimelineItem>("timeline") ?: emptyList()
                val paymentInfo = arguments?.getParcelable<PaymentInfo>("paymentInfo")
                val formSchema = arguments?.getParcelableArrayList<FormItem>("formSchema") ?: emptyList()
                val formAnswer = arguments?.getSerializable("formAnswer") as? Map<String, Any> ?: emptyMap()

                if (item != null && paymentInfo != null) {
                    RequestDetailScreen(
                        item = item,
                        timeline = timeline,
                        paymentInfo = paymentInfo,
                        formSchema = formSchema,
                        formAnswer = formAnswer,
                        onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() }
                    )
                } else {
                    Text("데이터를 불러오지 못했습니다.")
                }
            }
        }
    }
}

@Composable
fun RequestDetailScreen(
    item: RequestItem,
    timeline: List<TimelineItem>,
    paymentInfo: PaymentInfo,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8))
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 타이틀 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
        ) {
            // 왼쪽 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_left_vector),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickable { onBackClick() }
            )

            // 가운데 텍스트
            Text(
                text = "상세정보",
                fontSize = 20.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                fontFamily = notoSansKR,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            RequestDetailItem(item = item)
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
