package com.example.commit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.commit.ui.request.notoSansKR

val CommitTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = notoSansKR,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = notoSansKR,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = notoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = notoSansKR,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 12.sp
    )
)

