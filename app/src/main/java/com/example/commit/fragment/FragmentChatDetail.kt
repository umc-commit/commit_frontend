package com.example.commit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.commit.activity.MainActivity
import com.example.commit.ui.Theme.CommitTheme
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

        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    ChatRoomScreen(
                        commissionTitle = chatName,
                        onPayClick = { println("결제 클릭") }
                    )
                }
            }
        }
    }
}
