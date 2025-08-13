package com.example.commit.ui.request.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.commit.connection.dto.FormItem
import com.example.commit.ui.Theme.CommitTypography
import com.google.gson.JsonArray
import com.google.gson.JsonElement

@Composable
fun FormAnswerSection(
    formSchema: List<FormItem>
) {
    val formOnly = formSchema.filterNot { item ->
        val label = item.label
        val text = item.value.toReadable()
        label.contains("내용", ignoreCase = true) ||
                label.contains("이미지", ignoreCase = true) ||
                isImageUrl(text)
    }

    val note = formSchema
        .firstOrNull { it.label.contains("내용", ignoreCase = true) }
        ?.value?.toReadable()
        ?.takeIf { it.isNotBlank() }

    val allImages: List<String> = buildList {
        addAll(formSchema.flatMap { extractImageUrls(it.value) })
        addAll(
            formSchema.mapNotNull {
                it.takeIf { f -> f.label.contains("이미지", ignoreCase = true) }
                    ?.value?.toReadable()
                    ?.takeIf { s -> isImageUrl(s) }
            }
        )
    }.distinct()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "신청 폼",
            style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        formOnly.forEachIndexed { index, item ->
            val textValue = item.value.toReadable()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "${index + 1}. ${item.label}",
                        style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = textValue.ifBlank { "응답 없음" },
                        style = CommitTypography.labelLarge,
                        color = Color(0xFF555555)
                    )
                }
            }
        }

        // ------------------ 신청 내용 ------------------
        if (!note.isNullOrBlank() || allImages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "신청 내용",
                style = CommitTypography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5F5))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (allImages.isNotEmpty()) {
                        ImageGallerySmall(allImages)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (!note.isNullOrBlank()) {
                        Text(
                            text = note,
                            style = CommitTypography.labelLarge,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageGallerySmall(urls: List<String>) {
    val sizeTwoStack = 96.dp
    val sizeThumb = 80.dp
    val gap = 6.dp

    when (urls.size) {
        2 -> {
            Column(
                verticalArrangement = Arrangement.spacedBy(gap),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                urls.forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "첨부 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(sizeTwoStack)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
        else -> {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(gap),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(urls) { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = "첨부 이미지",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(sizeThumb)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }
    }
}

/** JsonElement → 문자열 변환 */
private fun JsonElement?.toReadable(): String {
    if (this == null) return ""
    return when {
        isJsonNull -> ""
        isJsonPrimitive -> asJsonPrimitive.asString.orEmpty()
        isJsonArray -> asJsonArray.joinToString(", ") { it.asString.orEmpty() }
        isJsonObject -> asJsonObject.toString()
        else -> toString()
    }
}

/** JsonElement → 문자열 리스트 */
private fun JsonElement?.asStringList(): List<String> {
    if (this == null || isJsonNull) return emptyList()
    return when {
        isJsonArray -> (this as JsonArray).mapNotNull {
            when {
                it.isJsonPrimitive -> it.asJsonPrimitive.asString
                else -> it.toString()
            }
        }
        isJsonPrimitive -> listOf(asJsonPrimitive.asString)
        else -> listOf(toString())
    }.filter { it.isNotBlank() }
}

/** 이미지 URL 추출 */
private fun extractImageUrls(value: JsonElement?): List<String> {
    val rawList = value.asStringList()
        .flatMap { s ->
            s.split(',', ' ', '\n', '\t')
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }
    return rawList.filter { isImageUrl(it) }
}

/** 이미지 URL 판별 */
private fun isImageUrl(s: String): Boolean {
    if (s.isBlank()) return false
    val lower = s.lowercase()
    val hasScheme = lower.startsWith("http://") || lower.startsWith("https://")
    val looksLikeImage =
        lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".webp") ||
                lower.contains("=png") || lower.contains("=jpg") || lower.contains("=jpeg") || lower.contains("=webp")
    return hasScheme && looksLikeImage
}
