package com.example.commit.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.commit.R
import com.example.commit.data.model.Artist
import com.example.commit.data.model.Commission
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.FormCheck.FormCheckScreen
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.viewmodel.CommissionFormViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FormCheckActivity : ComponentActivity() {

    private val formVM: CommissionFormViewModel by viewModels()

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
        val artistProfileImage = intent.getStringExtra("artistProfileImage")

        val formSchemaJson = intent.getStringExtra("formSchemaJson")
        val formAnswerJson = intent.getStringExtra("formAnswerJson")

        val gson = Gson()

        // formSchema, formAnswer 파싱
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
            profileImageUrl = artistProfileImage,
            name = artistNickname,
            message = "",
            time = "",
            isNew = false,
            title = title
        )

        val requestItem = RequestItem(
            requestId = requestId,
            status = "PENDING",
            title = title,
            price = totalPrice,
            thumbnailImageUrl = thumbnailUrl,
            progressPercent = 0,
            createdAt = createdAt,
            artist = Artist(id = artistId, nickname = artistNickname),
            commission = Commission(id = commissionId)
        )

        Log.d("FormCheckActivity", "onCreate commissionId=$commissionId, requestId=$requestId")

        // requestId 우선 호출, 없으면 commissionId로 폴백
        when {
            requestId > 0 -> {
                formVM.getSubmittedRequestForms(requestId.toString(), this)
            }
         /* commissionId > 0 -> {
            formVM.getSubmittedCommissionForm(commissionId.toString(), this)
            }*/
            else -> {
                Log.w("FormCheckActivity", "유효한 requestId/commissionId 없음 → API 호출 skip")
            }
        }

        setContent {
            CommitTheme {
                FormCheckScreen(
                    chatItem = chatItem,
                    requestItem = requestItem,
                    formSchema = formSchema,
                    formAnswer = formAnswer,
                    onBackClick = { finish() },
                    viewModel = formVM
                )
            }
        }
    }
}
