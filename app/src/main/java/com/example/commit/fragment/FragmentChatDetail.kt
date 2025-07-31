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
                    ChatRoomScreen(
                        commissionTitle = chatName,
                        authorName = authorName,
                        onPayClick = {
                            // 삭제 로직 구현 (예: DB 삭제 후 popBackStack)
                            parentFragmentManager.popBackStack()
                        },
                        onBackClick = {
                            parentFragmentManager.popBackStack()
                        },
                        onSettingClick = {
                            showBottomSheet.value = true
                        }
                    )
                    if (showBottomSheet.value) {
                        val activity = requireActivity() as AppCompatActivity

                        ChatOptionDialog(
                            onDismiss = { showBottomSheet.value = false }
                        )
                    }
                }
            }
        }
    }
}