package com.example.commit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.FormCheck.FormCheckScreen
import com.example.commit.ui.Theme.CommitTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FormCheckDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 전체 화면 스타일 설정
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onStart() {
        super.onStart()
        // Dialog window 크기 설정
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window.statusBarColor = android.graphics.Color.WHITE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val commissionTitle = arguments?.getString("commissionTitle") ?: ""
                    val artistName = arguments?.getString("artistName") ?: ""
                    val formSchemaJson = arguments?.getString("formSchema") ?: "{}"
                    val formAnswerJson = arguments?.getString("formAnswer") ?: "{}"
                    val artistProfileImage = arguments?.getString("artistProfileImage")?: ""

                    // JSON을 파싱하여 필요한 데이터 구조로 변환
                    val formSchema = try {
                        val gson = Gson()
                        val mapType = object : TypeToken<Map<String, Any>>() {}.type
                        val schemaMap: Map<String, Any> = gson.fromJson(formSchemaJson, mapType)
                        val fieldsList = schemaMap["fields"] as? List<*> ?: emptyList<Any>()
                        
                        fieldsList.mapNotNull { field ->
                            val fieldMap = field as? Map<*, *>
                            if (fieldMap != null) {
                                FormItem(
                                    id = (fieldMap["id"] as? String)?.toIntOrNull() ?: 0,
                                    type = fieldMap["type"] as? String ?: "",
                                    label = fieldMap["label"] as? String ?: "",
                                    options = emptyList()
                                )
                            } else null
                        }
                    } catch (e: Exception) {
                        emptyList<FormItem>()
                    }

                    val formAnswer = try {
                        val gson = Gson()
                        val mapType = object : TypeToken<Map<String, Any>>() {}.type
                        gson.fromJson<Map<String, Any>>(formAnswerJson, mapType) ?: emptyMap()
                    } catch (e: Exception) {
                        emptyMap<String, Any>()
                    }

                    // 더미 데이터 생성 (실제로는 API에서 가져온 데이터 사용)
                    val chatItem = ChatItem(
                        profileImageRes = com.example.commit.R.drawable.ic_profile, // 기본 프로필 이미지 사용
                        profileImageUrl = artistProfileImage,
                        name = artistName,
                        message = "",
                        time = "",
                        isNew = false,
                        title = commissionTitle
                    )

                    val requestItem = RequestItem(
                        requestId = 1,
                        status = "진행중",
                        title = commissionTitle,
                        price = 0,
                        thumbnailImageUrl = "",
                        progressPercent = 0,
                        createdAt = "",
                        artist = com.example.commit.data.model.Artist(
                            id = 1,
                            nickname = artistName
                        ),
                        commission = com.example.commit.data.model.Commission(
                            id = 1
                        )
                    )

                    // 안전한 영역 확보를 위한 Box
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                    ) {
                        FormCheckScreen(
                            chatItem = chatItem,
                            requestItem = requestItem,
                            formSchema = formSchema,
                            formAnswer = formAnswer,
                            onBackClick = { dismiss() }
                        )
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(
            commissionTitle: String,
            artistName: String,
            formSchema: String,
            formAnswer: String
        ): FormCheckDialogFragment {
            return FormCheckDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("commissionTitle", commissionTitle)
                    putString("artistName", artistName)
                    putString("formSchema", formSchema)
                    putString("formAnswer", formAnswer)
                }
            }
        }
    }
}
