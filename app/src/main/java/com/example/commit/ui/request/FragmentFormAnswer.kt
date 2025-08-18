package com.example.commit.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.commit.connection.dto.FormItem
import com.example.commit.ui.request.components.FormAnswerSection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FragmentFormAnswer : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val gson = Gson()

                // 1) 스키마 복원
                val schemaJson = arguments?.getString("formSchemaJson").orEmpty()
                val schemaType = object : TypeToken<List<FormItem>>() {}.type
                val formSchema: List<FormItem> =
                    if (schemaJson.isNotBlank()) gson.fromJson(schemaJson, schemaType) else emptyList()

                // 2) 답변 복원
                val answerJson = arguments?.getString("formAnswerJson").orEmpty()
                val answerType = object : TypeToken<Map<String, Any>>() {}.type
                val formAnswer: Map<String, Any> =
                    if (answerJson.isNotBlank()) gson.fromJson(answerJson, answerType) else emptyMap()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("신청서 보기", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    FormAnswerSection(
                        formSchema = formSchema,
                        formAnswer = formAnswer
                    )
                }
            }
        }
    }
}
