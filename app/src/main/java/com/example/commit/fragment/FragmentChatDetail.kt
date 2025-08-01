package com.example.commit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chatlist.ChatDeleteFragment
import com.example.commit.ui.chatlist.DeleteOptionBottomSheet
import com.example.commit.ui.chatroom.ChatOptionDialog
import com.example.commit.ui.chatroom.ChatRoomScreen
import com.example.commit.ui.FormCheck.FormCheckBottomSheet
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem

class FragmentChatDetail : Fragment() {
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val chatName = arguments?.getString("chatName") ?: "채팅방"
        val authorName = arguments?.getString("authorName") ?: "익명"

        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    val showFormCheckSheet = remember { mutableStateOf(false) }
                    
                    // FormCheck 데이터 준비
                    val requestItem = RequestItem(
                        requestId = 1,
                        status = "신청완료",
                        title = chatName,
                        price = 10000,
                        thumbnailImage = "",
                        artist = com.example.commit.data.model.Artist(1, authorName),
                        createdAt = "2024.06.02"
                    )
                    
                    val formSchema = listOf(
                        FormItem(
                            label = "신청 내용",
                            type = "text",
                            options = emptyList()
                        )
                    )
                    
                    val formAnswer = mapOf(
                        "신청 내용" to "빠르게 작업해주세요!"
                    )
                    
                    val chatItem = ChatItem(
                        profileImageRes = R.drawable.ic_profile,
                        name = authorName,
                        message = "",
                        title = chatName,
                        time = "",
                        isNew = false
                    )
                    
                    ChatRoomScreen(
                        commissionTitle = chatName,
                        authorName = authorName,
                        onPayClick = {
                            if (isAdded && !isDetached) {
                                parentFragmentManager.popBackStack()
                            }
                        },
                        onFormCheckClick = {
                            showFormCheckSheet.value = true
                        },
                        onBackClick = {
                            // 채팅리스트로 돌아가기
                            if (isAdded && !isDetached) {
                                parentFragmentManager.popBackStack()
                            }
                        },
                        onSettingClick = {
                            showBottomSheet.value = true
                        }
                    )
                    
                    if (showBottomSheet.value) {
                        val activity = requireActivity() as? AppCompatActivity
                        if (activity != null && isAdded && !isDetached) {
                            ChatOptionDialog(
                                onDismiss = { showBottomSheet.value = false }
                            )
                        }
                    }
                    
                    if (showFormCheckSheet.value) {
                        FormCheckBottomSheet(
                            chatItem = chatItem,
                            requestItem = requestItem,
                            formSchema = formSchema,
                            formAnswer = formAnswer,
                            onDismiss = { showFormCheckSheet.value = false }
                        )
                    }
                }
            }
        }
    }
}