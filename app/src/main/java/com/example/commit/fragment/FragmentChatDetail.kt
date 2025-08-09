package com.example.commit.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.activity.author.AuthorProfileActivity
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chatlist.ChatDeleteFragment
import com.example.commit.ui.chatlist.DeleteOptionBottomSheet
import com.example.commit.ui.chatroom.ChatOptionDialog
import com.example.commit.ui.chatroom.ChatRoomScreen
import com.example.commit.ui.FormCheck.FormCheckBottomSheet
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.viewmodel.ChatViewModel

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
        val chatroomId = arguments?.getInt("chatroomId", 1) ?: 1 // 기본값 1

        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    val showFormCheckSheet = remember { mutableStateOf(false) }
                    val chatViewModel: ChatViewModel = viewModel()
                    
                    // 채팅방 초기화
                    LaunchedEffect(chatroomId) {
                        chatViewModel.initializeChatroom(chatroomId)
                        // 기존 메시지 로드 시도 (실패하면 빈 상태로 시작)
                        chatViewModel.loadMessages(requireContext(), chatroomId)
                    }
                    
                    // FormCheck 데이터 준비
              /*      val requestItem = RequestItem(
                        requestId = 1,
                        status = "신청완료",
                        title = chatName,
                        price = 10000,
                        thumbnailImage = "",
                        artist = com.example.commit.data.model.Artist(1, authorName),
                        createdAt = "2024.06.02"
                    )*/
                    
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
                        chatViewModel = chatViewModel, // ChatViewModel 전달
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
                        },
                        onProfileClick = {
                            // AuthorProfileActivity로 이동
                            val intent = Intent(requireContext(), AuthorProfileActivity::class.java)
                            startActivity(intent)
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
                    
                    /*if (showFormCheckSheet.value) {
                        FormCheckBottomSheet(
                            chatItem = chatItem,
                            requestItem = requestItem,
                            formSchema = formSchema,
                            formAnswer = formAnswer,
                            onDismiss = { showFormCheckSheet.value = false }
                        )
                    }*/
                }
            }
        }
    }
}