package com.example.commit.ui.point

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import com.example.commit.ui.point.components.AgreementSection
import com.example.commit.ui.point.components.ChargeButton
import com.example.commit.ui.point.components.PaymentMethodList
import com.example.commit.ui.point.components.PointBalanceSection
import com.example.commit.ui.point.components.PointOptionList

@Composable
fun PointChargeScreen(
    onCancelClicked: () -> Unit,
    onChargeSuccess: () -> Unit
) {
    var selectedPoint by remember { mutableStateOf<Int?>(null) }
    var selectedPayment by remember { mutableStateOf<String?>(null) }
    var agreed by remember { mutableStateOf(false) }

    val isChargeEnabled = agreed && selectedPoint != null && selectedPayment != null

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 상단 헤더 (기존 아이콘 그대로 사용)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cancell),
                contentDescription = "닫기",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 16.dp)
                    .height(24.dp)
                    .clickable { onCancelClicked() }
            )

            Text(
                text = "포인트 충전",
                style = CommitTypography.headlineSmall
            )
        }

        // 본문 스크롤 영역
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_point_banner),
                contentDescription = "포인트 배너",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            PointBalanceSection(currentPoint = 5000)

            Spacer(modifier = Modifier.height(24.dp))

            PointOptionList(
                selectedPoint = selectedPoint,
                onSelect = { selectedPoint = it }
            )

            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            PaymentMethodList(
                selectedPayment = selectedPayment,
                onSelect = { selectedPayment = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)

            Spacer(modifier = Modifier.height(24.dp))

            // 동의 섹션 (기존 구현 그대로 사용)
            AgreementSection(
                amount = selectedPoint ?: 0,
                agreed = agreed,
                onAgreeChanged = { agreed = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 결제 버튼(위에 투명 클릭 레이어로 onClick 처리)
            Box(modifier = Modifier.fillMaxWidth()) {
                ChargeButton(enabled = isChargeEnabled)

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            enabled = isChargeEnabled,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onChargeSuccess()
                        }
                )
            }
        }
    }
}
