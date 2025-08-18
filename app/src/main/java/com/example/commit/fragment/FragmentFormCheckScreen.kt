package com.example.commit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.ui.FormCheck.FormCheckScreen
import com.example.commit.ui.Theme.CommitTheme
import com.google.gson.Gson
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.viewmodel.CommissionFormViewModel

class FragmentFormCheckScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val gson = Gson()
                    
                    val requestItemJson = arguments?.getString("requestItem")
                    val formSchemaJson = arguments?.getString("formSchema")
                    val formAnswerJson = arguments?.getString("formAnswer")
                    val artistProfileImage = arguments?.getString("artistProfileImage")


                    val requestItem = gson.fromJson(requestItemJson, RequestItem::class.java)
                    val formSchema = gson.fromJson(formSchemaJson, Array<FormItem>::class.java).toList()
                    val formAnswer = gson.fromJson(formAnswerJson, Map::class.java) as Map<String, Any>
                    
                    // 임시 ChatItem 생성
                    val chatItem = ChatItem(
                        profileImageRes = com.example.commit.R.drawable.ic_profile,
                        profileImageUrl = artistProfileImage,
                        name = "키르",
                        message = "",
                        title = requestItem.title,
                        time = "",
                        isNew = false
                    )

                    val formVM: CommissionFormViewModel = viewModel()
                    FormCheckScreen(
                        chatItem = chatItem,
                        requestItem = requestItem,
                        formSchema = formSchema,
                        formAnswer = formAnswer,
                        onBackClick = { requireActivity().onBackPressedDispatcher.onBackPressed()},
                        viewModel = formVM
                    )
                }
            }
        }
    }
} 