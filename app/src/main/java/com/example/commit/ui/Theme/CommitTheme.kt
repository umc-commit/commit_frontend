package com.example.commit.ui.Theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.commit.ui.request.notoSansKR

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF17D5C6),
    onPrimary = Color.White,
    secondary = Color(0xFF4D4D4D),
    background = Color(0xFFF9F9F9),
    surface = Color.White,
    onSurface = Color.Black
)

private val AppTypography = Typography(
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

@Composable
fun CommitTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        content = content
    )
}
