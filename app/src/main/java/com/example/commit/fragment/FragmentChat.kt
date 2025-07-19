package com.example.commit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.databinding.FragmentChatBinding
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chat.ChatDeleteFragment
import com.example.commit.ui.chat.ChatListScreen
import com.example.commit.ui.chat.DeleteOptionBottomSheet

class FragmentChat : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }

                    val chatItems = listOf(
                        ChatItem(R.drawable.ic_profile, "키르", "[결제 요청] 낙서 타임 커미션", "방금 전", true),
                        ChatItem(R.drawable.ic_profile, "브로콜리", "[커미션 완료] 일러스트 타입", "2일 전", false)
                    )

                    // ✅ ChatListScreen에 onSettingClick 인자를 넘김
                    ChatListScreen(
                        chatItems = chatItems,
                        onItemClick = { clickedItem ->
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.Nav_Frame, FragmentChatDetail().apply {
                                    arguments = bundleOf("chatName" to clickedItem.name)
                                })
                                .addToBackStack(null)
                                .commit()
                        },
                        onSettingClick = {
                            showBottomSheet.value = true
                        }
                    )

                    // ✅ 바텀시트 조건부 표시
                    if (showBottomSheet.value) {
                        val activity = requireActivity() as AppCompatActivity

                        DeleteOptionBottomSheet(
                            onDismiss = { showBottomSheet.value = false },
                            onDeleteClick = {
                                showBottomSheet.value = false
                                activity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.Nav_Frame, ChatDeleteFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        )
                    }
                }
            }
        }
    }
}


