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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.activity.author.AuthorProfileActivity
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chatroom.ChatOptionDialog
import com.example.commit.ui.chatroom.ChatRoomScreen
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.viewmodel.ChatViewModel
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentPostChatDetail : Fragment() {
    private var _binding: View? = null
    
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment가 제거될 때 BottomNavigation 다시 보이기
        (activity as? MainActivity)?.showBottomNav(true)
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
        val hasSubmittedApplication = arguments?.getBoolean("hasSubmittedApplication", false) ?: false
        
        Log.d("FragmentPostChatDetail", "채팅방 정보 - ID: $chatroomId, 제목: $chatName, 작가: $authorName, 출처: $sourceFragment, 신청서 제출됨: $hasSubmittedApplication")

        return ComposeView(requireContext()).apply {
            _binding = this
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    val chatViewModel: ChatViewModel = viewModel()
                    
                    // 채팅방 초기화 (동적으로 신청서 제출 상태 설정)
                    LaunchedEffect(chatroomId) {
                        chatViewModel.setChatroomId(chatroomId)
                        chatViewModel.setApplicationStatus(hasSubmittedApplication)
                        chatViewModel.loadMessages(requireContext(), chatroomId)
                    }
                    
                    ChatRoomScreen(
                        commissionTitle = chatName,
                        authorName = authorName,
                        chatroomId = chatroomId,
                        chatViewModel = chatViewModel,
                        onPayClick = {
                            if (isAdded && !isDetached) {
                                parentFragmentManager.popBackStack()
                            }
                        },
                        onFormCheckClick = {
                            // 신청서 확인하기 클릭 시 FormCheckScreen 호출
                            Log.d("FragmentPostChatDetail", "신청서 확인하기 클릭됨 - commissionId: $commissionId")
                            showFormCheckScreen(commissionId)
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
    
    // 신청서 확인 화면 표시
    private fun showFormCheckScreen(commissionId: Int) {
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        Log.d("FragmentPostChatDetail", "제출된 신청서 조회 시작 - commissionId: $commissionId")
        
        api.getSubmittedCommissionForm(commissionId).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("FragmentPostChatDetail", "신청서 조회 성공: ${data}")
                        
                        // FormCheckDialogFragment로 화면 전환
                        val formSchemaJson = Gson().toJson(data.formSchema)
                        val formAnswerJson = if (data.formAnswer != null) {
                            Gson().toJson(data.formAnswer)
                        } else {
                            "{}" // 빈 객체
                        }
                        
                        val dialog = FormCheckDialogFragment.newInstance(
                            commissionTitle = data.commission.title,
                            artistName = data.commission.artist?.nickname ?: "작가명",
                            formSchema = formSchemaJson,
                            formAnswer = formAnswerJson
                        )
                        
                        if (isAdded && !isDetached) {
                            dialog.show(parentFragmentManager, "FormCheckDialog")
                        }
                    } else {
                        Log.e("FragmentPostChatDetail", "신청서 조회 실패: 데이터 없음")
                        android.widget.Toast.makeText(
                            requireContext(),
                            "신청서를 불러올 수 없습니다",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("FragmentPostChatDetail", "신청서 조회 실패: ${response.code()}")
                    android.widget.Toast.makeText(
                        requireContext(),
                        "신청서 조회에 실패했습니다",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>,
                t: Throwable
            ) {
                Log.e("FragmentPostChatDetail", "신청서 조회 네트워크 오류", t)
                android.widget.Toast.makeText(
                    requireContext(),
                    "네트워크 오류가 발생했습니다",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    
    companion object {
        fun newInstance(
            chatName: String,
            authorName: String,
            commissionId: Int,
            hasSubmittedApplication: Boolean = false,
            chatroomId: Int = 1 // 기본값
        ): FragmentPostChatDetail {
            return FragmentPostChatDetail().apply {
                arguments = Bundle().apply {
                    putString("chatName", chatName)
                    putString("authorName", authorName)
                    putInt("commissionId", commissionId)
                    putBoolean("hasSubmittedApplication", hasSubmittedApplication)
                    putInt("chatroomId", chatroomId)
                    putString("sourceFragment", "CommissionForm")
                }
            }
        }
    }
} 