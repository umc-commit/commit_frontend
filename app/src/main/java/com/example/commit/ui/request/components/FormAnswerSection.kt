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
import androidx.compose.runtime.remember
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
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive

/**
 * 신청서 보기 섹션
 * - formSchema: 스키마(레이블/타입/옵션)
 * - formAnswer: 사용자가 작성한 값(없으면 빈 map). label 또는 id 키로 매칭됨.
 */
@Composable
fun FormAnswerSection(
    formSchema: List<FormItem>,
    formAnswer: Map<String, Any> = emptyMap()
) {
    val gson = remember { Gson() }

    // item.value(서버가 value를 넣어준 경우) > formAnswer(로컬/다른 API) 순으로 값 선택
    fun resolvedValue(item: FormItem): JsonElement? {
        val schemaValue = item.value
        if (schemaValue != null && !schemaValue.isJsonNull && schemaValue.toReadable().isNotBlank()) {
            return schemaValue
        }
        // formAnswer에서 label, id, 흔한 키(files/images/urls/note/content) 순으로 탐색
        val idKey = try { item.id?.toString() } catch (_: Throwable) { null }
        val candidates = listOfNotNull(
            item.label,
            idKey,
            "files", "images", "imageUrls", "urls", "note", "content"
        )
        val raw: Any? = candidates.firstNotNullOfOrNull { key -> formAnswer[key] }
        return raw?.let { anyToJsonElement(gson, it) }
    }

    val formOnly = formSchema.filterNot { item ->
        val label = item.label ?: ""
        val text = resolvedValue(item).toReadable()
        label.contains("내용", ignoreCase = true) ||
                label.contains("이미지", ignoreCase = true) ||
                isImageUrl(text) ||
                item.type.equals("file", ignoreCase = true)
    }

    val note = formSchema
        .firstOrNull { (it.label ?: "").contains("내용", ignoreCase = true) }
        ?.let { resolvedValue(it).toReadable() }
        ?.takeIf { it.isNotBlank() }

    val allImages: List<String> = buildList {
        addAll(formSchema.flatMap { extractImageUrls(resolvedValue(it)) })
        addAll(
            formSchema.mapNotNull {
                val lbl = it.label ?: ""
                resolvedValue(it).toReadable()
                    .takeIf { s -> lbl.contains("이미지", ignoreCase = true) && isImageUrl(s) }
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
            val textValue = resolvedValue(item).toReadable()

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
                        text = "${index + 1}. ${item.label ?: ""}",
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

        // ------------------ 신청 내용 + 이미지 ------------------
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

/** 가로 썸네일 갤러리 */
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

/** Any -> JsonElement 안전 변환 */
private fun anyToJsonElement(gson: Gson, value: Any): JsonElement {
    return when (value) {
        is JsonElement -> value
        is String -> JsonPrimitive(value)
        is Number -> JsonPrimitive(value)
        is Boolean -> JsonPrimitive(value)
        is List<*> -> {
            val arr = JsonArray()
            value.forEach { v -> arr.add(anyToJsonElement(gson, v ?: JsonNull.INSTANCE)) }
            arr
        }
        is Array<*> -> {
            val arr = JsonArray()
            value.forEach { v -> arr.add(anyToJsonElement(gson, v ?: JsonNull.INSTANCE)) }
            arr
        }
        null -> JsonNull.INSTANCE
        else -> gson.toJsonTree(value)
    }
}

/** JsonElement → 문자열 변환 */
private fun JsonElement?.toReadable(): String {
    if (this == null || isJsonNull) return ""
    return when {
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
