package com.example.commit.fragment

import android.os.Bundle
import android.util.Log
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
import com.example.commit.ui.chatroom.ChatOptionDialog
import com.example.commit.ui.chatroom.ChatRoomScreen
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem

class FragmentPostChatDetail : Fragment() {
    private var _binding: View? = null
    
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val chatName = arguments?.getString("chatName") ?: "채팅방"
        val authorName = arguments?.getString("authorName") ?: "익명"

        return ComposeView(requireContext()).apply {
            _binding = this
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    
                    ChatRoomScreen(
                        commissionTitle = chatName,
                        authorName = authorName,
                        onPayClick = {
                            if (isAdded && !isDetached) {
                                parentFragmentManager.popBackStack()
                            }
                        },
                        onFormCheckClick = {
                            // Post 채팅방에서는 신청서 확인 기능 비활성화
                            android.widget.Toast.makeText(
                                requireContext(),
                                "신청서 확인 기능은 신청 후에 사용할 수 있습니다",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        },
                        onBackClick = {
                            // PostHeaderSection으로 돌아가기
                            Log.d("FragmentPostChatDetail", "뒤로가기 클릭됨")
                            
                            android.widget.Toast.makeText(
                                requireContext(),
                                "이전 화면으로 돌아갑니다",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            
                            try {
                                // PostHeaderSection을 포함하는 Fragment로 전환
                                val fragment = com.example.commit.fragment.FragmentHome().apply {
                                    arguments = Bundle().apply {
                                        putBoolean("show_post_detail", true)
                                        putString("post_title", chatName)
                                    }
                                }
                                
                                if (isAdded && !isDetached) {
                                    parentFragmentManager.beginTransaction()
                                        .replace(com.example.commit.R.id.Nav_Frame, fragment)
                                        .addToBackStack(null)
                                        .commit()
                                }
                            } catch (e: Exception) {
                                Log.e("FragmentPostChatDetail", "화면 전환 실패", e)
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
                }
            }
        }
    }
} 