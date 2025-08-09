package com.example.commit.fragment

import android.content.Intent
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
import com.example.commit.activity.author.AuthorProfileActivity
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
        val chatroomId = arguments?.getInt("chatroomId", -1) ?: -1
        val sourceFragment = arguments?.getString("sourceFragment") ?: ""
        val commissionId = arguments?.getInt("commissionId", -1) ?: -1
        
        Log.d("FragmentPostChatDetail", "채팅방 정보 - ID: $chatroomId, 제목: $chatName, 작가: $authorName, 출처: $sourceFragment")

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
                            Log.d("FragmentPostChatDetail", "뒤로가기 클릭됨 - 출처: $sourceFragment")
                            
                            if (isAdded && !isDetached) {
                                when (sourceFragment) {
                                    "FragmentHome" -> {
                                        // FragmentHome에서 PostScreen을 다시 표시
                                        val fragment = com.example.commit.fragment.FragmentHome().apply {
                                            arguments = Bundle().apply {
                                                putBoolean("show_post_detail", true)
                                                putInt("commission_id", commissionId)
                                            }
                                        }
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.Nav_Frame, fragment)
                                            .commit()
                                    }
                                    "FragmentPostScreen" -> {
                                        // FragmentPostScreen으로 돌아가기
                                        val fragment = com.example.commit.ui.post.FragmentPostScreen.newInstance(commissionId)
                                        parentFragmentManager.beginTransaction()
                                            .replace(R.id.Nav_Frame, fragment)
                                            .commit()
                                    }
                                    else -> {
                                        // 기본적으로 popBackStack 사용
                                        parentFragmentManager.popBackStack()
                                    }
                                }
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
                }
            }
        }
    }
} 