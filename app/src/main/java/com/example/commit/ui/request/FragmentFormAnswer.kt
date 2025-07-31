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
import com.example.commit.data.model.FormItem
import com.example.commit.ui.request.components.FormAnswerSection

class FragmentFormAnswer : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val formSchema = arguments?.getParcelableArrayList<FormItem>("formSchema") ?: emptyList()
                val formAnswer = arguments?.getSerializable("formAnswer") as? Map<String, Any> ?: emptyMap()

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

    companion object {
        fun newInstance(
            formSchema: ArrayList<FormItem>,
            formAnswer: HashMap<String, Any>
        ): FragmentFormAnswer {
            return FragmentFormAnswer().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("formSchema", formSchema)
                    putSerializable("formAnswer", formAnswer)
                }
            }
        }
    }
}
