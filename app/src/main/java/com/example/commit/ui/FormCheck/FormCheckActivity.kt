package com.example.commit.ui.FormCheck

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.commit.ui.Theme.CommitTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.commit.R

// FormCheckScreen이 기대하는 타입 (data.model 쪽)을 그대로 사용
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.OptionItem
import com.example.commit.data.model.RequestItem as DMRequestItem
import com.example.commit.data.model.Artist as DMArtist
import com.example.commit.data.model.Commission as DMCommission

class FormCheckActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- Intent로 받은 값들 ---
        val requestId = intent.getIntExtra("requestId", -1)
        val commissionId = intent.getIntExtra("commissionId", -1)
        val title = intent.getStringExtra("title") ?: ""
        val artistId = intent.getIntExtra("artistId", -1)
        val artistNickname = intent.getStringExtra("artistNickname") ?: ""
        val thumbnailUrl = intent.getStringExtra("thumbnailUrl") ?: ""
        val totalPrice = intent.getIntExtra("totalPrice", 0)
        val createdAt = intent.getStringExtra("createdAt") ?: ""

        val formSchemaJson = intent.getStringExtra("formSchemaJson")
        val formAnswerJson = intent.getStringExtra("formAnswerJson")

        val gson = Gson()

        // formSchema, formAnswer 파싱 (없으면 빈 값으로)
        val formSchema: List<FormItem> = formSchemaJson?.let {
            val type = object : TypeToken<List<FormItem>>() {}.type
            runCatching { gson.fromJson<List<FormItem>>(it, type) }.getOrDefault(emptyList())
        } ?: emptyList()

        val formAnswer: Map<String, Any> = formAnswerJson?.let {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            runCatching { gson.fromJson<Map<String, Any>>(it, type) }.getOrDefault(emptyMap())
        } ?: emptyMap()

        // TopBar 등에 쓸 ChatItem / RequestItem 구성
        val chatItem = ChatItem(
            profileImageRes = R.drawable.ic_profile,
            name = artistNickname,
            message = "",
            time = "",
            isNew = false,
            title = title
        )

        val requestItem = DMRequestItem(
            requestId = requestId,
            status = "PENDING",
            title = title,
            price = totalPrice,
            thumbnailImageUrl = thumbnailUrl,
            progressPercent = 0,
            createdAt = createdAt,
            artist = DMArtist(id = artistId, nickname = artistNickname),
            commission = DMCommission(id = commissionId)
        )

        setContent {
            CommitTheme {
                FormCheckScreen(
                    chatItem = chatItem,
                    requestItem = requestItem,
                    formSchema = formSchema,
                    formAnswer = formAnswer,
                    onBackClick = { finish() }
                )
            }
        }

        if (requestId <= 0) {
            // Toast.makeText(this, "요청 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show() //
        }
    }
}
