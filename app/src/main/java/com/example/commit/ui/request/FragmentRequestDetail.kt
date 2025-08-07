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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.data.model.*
import com.example.commit.ui.request.components.RequestDetailItem
import com.example.commit.ui.request.components.RequestDetailSectionList
import com.example.commit.ui.Theme.CommitTypography

class FragmentRequestDetail : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                /*val item = arguments?.getParcelable<RequestItem>("requestItem")*/
                val timeline = arguments?.getParcelableArrayList<TimelineItem>("timeline") ?: emptyList()
                val paymentInfo = arguments?.getParcelable<PaymentInfo>("paymentInfo")
                val formSchema = arguments?.getParcelableArrayList<FormItem>("formSchema") ?: emptyList()
                val formAnswer = arguments?.getSerializable("formAnswer") as? Map<String, Any> ?: emptyMap()

                if (/*item != null && */paymentInfo != null) {
                  /*  RequestDetailScreen(
                        item = item,
                        timeline = timeline,
                        paymentInfo = paymentInfo,
                        formSchema = formSchema,
                        formAnswer = formAnswer,
                        onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed() }
                    )*/
                } else {
                    Text("데이터를 불러오지 못했습니다.", style = CommitTypography.bodyMedium)
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
    onBackClick: () -> Unit,
    onFormAnswerClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // 바텀바 숨기기
    LaunchedEffect(Unit) {
        (context as? MainActivity)?.showBottomNav(false)
    }

    // 화면 나갈 때 바텀바 다시 보이기
    DisposableEffect(Unit) {
        onDispose {
            (context as? MainActivity)?.showBottomNav(true)
        }
    }

    val isCancel = item.status == "CANCEL"
    val isReject = item.status == "REJECT"

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
                onFormAnswerClick = {
                    val newFragment = FragmentFormAnswer.newInstance(
                        ArrayList(formSchema),
                        HashMap(formAnswer)
                    )
                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.Nav_Frame, newFragment)
                        .addToBackStack(null)
                        .commit()
                }
            )

            // 취소 또는 거절 상태가 아니면 하단 상세 정보 보여주기
            if (!isCancel && !isReject) {
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
