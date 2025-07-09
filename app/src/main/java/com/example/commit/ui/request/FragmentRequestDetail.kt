package com.example.commit.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.commit.data.model.*
import com.example.commit.ui.request.components.RequestDetailSectionList

class FragmentRequestDetail : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val timeline = arguments?.getParcelableArrayList<TimelineItem>("timeline") ?: emptyList()
                val paymentInfo = arguments?.getParcelable<PaymentInfo>("paymentInfo")
                val formSchema = arguments?.getParcelableArrayList<FormItem>("formSchema") ?: emptyList()
                val formAnswer = arguments?.getSerializable("formAnswer") as? Map<String, Any> ?: emptyMap()

                if (paymentInfo != null) {
                    RequestDetailContent(
                        timeline = timeline,
                        paymentInfo = paymentInfo,
                        formSchema = formSchema,
                        formAnswer = formAnswer
                    )
                } else {
                    Text("데이터를 불러오지 못했습니다.")
                }
            }
        }
    }
}

@Composable
fun RequestDetailContent(
    timeline: List<TimelineItem>,
    paymentInfo: PaymentInfo,
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8E8E8))
            .padding(16.dp)
    ) {
        RequestDetailSectionList(
            timeline = timeline,
            paymentInfo = paymentInfo,
            formSchema = formSchema,
            formAnswer = formAnswer
        )
    }
}
