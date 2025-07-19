package com.example.commit.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.Theme.CommitTheme

class ChatDeleteFragment : Fragment() {

    private val dummyChatList = listOf(
        ChatItem(R.drawable.ic_profile, "키르", "[결제 요청] 낙서 타임 커미션", "방금 전", true),
        ChatItem(R.drawable.ic_profile, "브로콜리", "[커미션 완료] 일러스트 타입", "2일 전", false)
    )

    private val selectedItems = mutableStateListOf<ChatItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    ChatDeleteScreen(
                        chatItems = dummyChatList,
                        selectedItems = selectedItems,
                        onItemToggle = {
                            if (selectedItems.contains(it)) selectedItems.remove(it)
                            else selectedItems.add(it)
                        },
                        onDeleteClick = {
                            // 삭제 로직 구현 (예: DB 삭제 후 popBackStack)
                            parentFragmentManager.popBackStack()
                        },
                        onBackClick = {
                            parentFragmentManager.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
