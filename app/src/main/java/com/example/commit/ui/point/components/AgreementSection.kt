// AgreementSection.kt

package com.example.commit.ui.point.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import com.example.commit.R
import com.example.commit.ui.Theme.CommitTypography
import androidx.compose.ui.unit.sp

@Composable
fun AgreementSection(
    amount: Int,
    agreed: Boolean,
    onAgreeChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "결제 금액",
            style = CommitTypography.headlineSmall
        )

        Text(
            text = "₩${"%,d".format(amount)}원",
            color = Color(0xFF17D5C6),
            style = CommitTypography.headlineSmall
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAgreeChanged(!agreed) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(
                id = if (agreed) R.drawable.ic_checkbox_checked else R.drawable.ic_checkbox_unchecked
            ),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "결제 내용을 확인하였으며, ",
            style = CommitTypography.bodyMedium.copy(fontSize = 8.sp)
        )

        Text(
            text = "서비스 이용약관",
            style = CommitTypography.bodyMedium.copy(
                fontSize = 8.sp,
                textDecoration = TextDecoration.Underline
            )
        )

        Text(
            text = "에 동의합니다.",
            style = CommitTypography.bodyMedium.copy(fontSize = 8.sp)
        )
    }
}
