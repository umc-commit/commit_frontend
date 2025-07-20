package com.example.commit.ui.point

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
    onCancelClicked: () -> Unit
) {
    var selectedPoint by remember { mutableStateOf<Int?>(null) }
    var selectedPayment by remember { mutableStateOf<String?>(null) }
    var agreed by remember { mutableStateOf(false) }

    val isChargeEnabled = agreed && selectedPoint != null && selectedPayment != null

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 상단 헤더 고정
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
                    .size(24.dp)
                    .clickable {
                        onCancelClicked()
                    }
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
                painter = painterResource(id = R.drawable.ic_point_rectangle),
                contentDescription = "포인트 직사각형",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
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

            AgreementSection(
                amount = selectedPoint ?: 0,
                agreed = agreed,
                onAgreeChanged = { agreed = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ChargeButton(enabled = isChargeEnabled)
        }
    }
}
