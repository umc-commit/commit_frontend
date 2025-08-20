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
import androidx.core.os.bundleOf
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
import com.example.commit.fragment.FormCheckDialogFragment
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.data.model.FormItem
import com.example.commit.data.model.RequestItem
import com.example.commit.viewmodel.ChatViewModel
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.dto.CommissionDetail
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentChatDetail : Fragment() {
    private var _binding: View? = null
    
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Fragment가 제거될 때 BottomNavigation 처리
        // 포스트에서 시작한 경우에는 숨김 상태 유지, 기존 채팅방인 경우에는 보이기
        val isNewChat = arguments?.getBoolean("isNewChat", false) ?: false
        if (!isNewChat) {
            (activity as? MainActivity)?.showBottomNav(true)
        }
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val chatName = arguments?.getString("chatName") ?: "채팅방"
        val authorName = arguments?.getString("authorName") ?: "익명"
        val chatroomId = arguments?.getInt("chatroomId", 1) ?: 1
        val artistId = arguments?.getInt("artistId", -1) ?: -1
        val artistProfileImage = arguments?.getString("artistProfileImage")
        val requestId = arguments?.getInt("requestId", -1) ?: -1
        val thumbnailUrl = arguments?.getString("thumbnailUrl") ?: ""
        
        // 포스트에서 시작한 채팅 관련 파라미터들
        val commissionId = arguments?.getInt("commissionId", -1) ?: -1
        val hasSubmittedApplication = arguments?.getBoolean("hasSubmittedApplication", false) ?: false
        val sourceFragment = arguments?.getString("sourceFragment") ?: ""
        val isNewChat = arguments?.getBoolean("isNewChat", false) ?: false
        
        Log.d("FragmentChatDetail", "채팅방 정보 - ID: $chatroomId, 제목: $chatName, 작가: $authorName, isNewChat: $isNewChat")

        return ComposeView(requireContext()).apply {
            _binding = this
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    val showFormCheckSheet = remember { mutableStateOf(false) }
                    val chatViewModel: ChatViewModel = viewModel()
                    
                    // 채팅방 초기화
                    LaunchedEffect(chatroomId) {
                        chatViewModel.setChatroomId(chatroomId)
                        if (isNewChat) {
                            chatViewModel.setApplicationStatus(hasSubmittedApplication)
                        }
                        // ✅ 실제 API에서 가져온 artistId 설정
                        if (artistId != -1) {
                            chatViewModel.setArtistId(artistId)
                        }
                        chatViewModel.loadMessages(requireContext(), chatroomId)
                    }
                    
                    // FormCheck 데이터 준비 (기존 채팅방용)
                    val formSchema = listOf(
                        FormItem(
                            id = 1,
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
                        profileImageUrl = artistProfileImage,
                        name = authorName,
                        message = "",
                        title = chatName,
                        time = "",
                        isNew = false
                    )
                    
                    ChatRoomScreen(
                        commissionTitle = chatName,
                        authorName = authorName,
                        chatroomId = chatroomId,
                        chatViewModel = chatViewModel,
                        authorProfileImageUrl = artistProfileImage,
                        commissionThumbnailUrl = thumbnailUrl,
                        onSeePostClick = {
                            // 글보기 버튼 클릭 시 PostScreen으로 이동
                            if (isNewChat && commissionId != -1) {
                                showPostScreen(commissionId)
                            }
                        },

                        onPayClick = {
                            if (isAdded && !isDetached) {
                                parentFragmentManager.popBackStack()
                            }
                        },
                        onFormCheckClick = {
                            if (isNewChat) {
                                // 포스트에서 시작한 채팅: 신청서 확인 기능
                                showFormCheckScreen(commissionId)
                            } else {
                                // 기존 채팅방: FormCheckBottomSheet
                                showFormCheckSheet.value = true
                            }
                        },
                        onBackClick = {
                            if (isAdded && !isDetached) {
                                if (isNewChat) {
                                    // 포스트에서 시작한 채팅: 출처별 뒤로가기 처리
                                    handleNewChatBackClick(sourceFragment, commissionId)
                                } else {
                                    // 기존 채팅방: 채팅리스트로 돌아가기
                                    (activity as? MainActivity)?.showBottomNav(true)
                                    parentFragmentManager.popBackStack()
                                }
                            }
                        },
                        onSettingClick = {
                            showBottomSheet.value = true
                        },
                        onProfileClick = {
                            val intent = Intent(requireContext(), AuthorProfileActivity::class.java)
                            if (isNewChat) {
                                // 포스트에서 시작한 채팅: 기본 프로필 화면
                                startActivity(intent)
                            } else {
                                // 기존 채팅방: artistId와 함께 전달
                                intent.putExtra("artistId", artistId)
                                startActivity(intent)
                            }
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
                            requestItem = RequestItem(
                                requestId = requestId,
                                status = "PENDING",
                                title = chatName,
                                price = 0,
                                thumbnailImageUrl = thumbnailUrl,
                                progressPercent = 0,
                                createdAt = "",
                                artist = com.example.commit.data.model.Artist(id = artistId, nickname = authorName),
                                commission = com.example.commit.data.model.Commission(id = 0)
                            ),
                            formSchema = formSchema,
                            formAnswer = formAnswer,
                            onDismiss = { showFormCheckSheet.value = false }
                        )
                    }
                }
            }
        }
    }
    
    // 포스트에서 시작한 채팅의 신청서 확인 화면 표시
    private fun showFormCheckScreen(commissionId: Int) {
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        Log.d("FragmentChatDetail", "제출된 신청서 조회 시작 - commissionId: $commissionId")
        
        api.getSubmittedCommissionForm(commissionId.toString()).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.SubmittedFormData>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("FragmentChatDetail", "신청서 조회 성공: ${data}")
                        
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
                        Log.e("FragmentChatDetail", "신청서 조회 실패: 데이터 없음")
                        android.widget.Toast.makeText(
                            requireContext(),
                            "신청서를 불러올 수 없습니다",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("FragmentChatDetail", "신청서 조회 실패: ${response.code()}")
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
                Log.e("FragmentChatDetail", "신청서 조회 네트워크 오류", t)
                android.widget.Toast.makeText(
                    requireContext(),
                    "네트워크 오류가 발생했습니다",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    
    // 포스트에서 시작한 채팅의 뒤로가기 처리
    private fun handleNewChatBackClick(sourceFragment: String, commissionId: Int) {
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
                    .addToBackStack(null)
                    .commit()
                // BottomNavigation 숨기기
                (activity as? MainActivity)?.showBottomNav(false)
            }
            "FragmentPostScreen" -> {
                // FragmentPostScreen으로 돌아가기
                val fragment = com.example.commit.ui.post.FragmentPostScreen.newInstance(commissionId)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.Nav_Frame, fragment)
                    .addToBackStack(null)
                    .commit()
                // BottomNavigation 숨기기
                (activity as? MainActivity)?.showBottomNav(false)
            }
            else -> {
                // 기본적으로 popBackStack 사용하되, 바텀바는 숨김 상태 유지
                parentFragmentManager.popBackStack()
                // popBackStack 후에도 바텀바 숨김 상태 유지
                (activity as? MainActivity)?.showBottomNav(false)
            }
        }
    }
    
    // PostScreen으로 이동
    private fun showPostScreen(commissionId: Int) {
        val fragment = com.example.commit.ui.post.FragmentPostScreen.newInstance(commissionId)
        parentFragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, fragment)
            .addToBackStack(null)
            .commit()
        (activity as? MainActivity)?.showBottomNav(false)
    }
    
    companion object {
        // 기존 채팅방용 (FragmentChatDetail)
        fun newInstance(
            chatName: String,
            authorName: String,
            chatroomId: Int = 1,
            artistId: Int = -1,
            artistProfileImage: String? = null,
            requestId: Int = -1,
            thumbnailUrl: String = ""
        ): FragmentChatDetail {
            return FragmentChatDetail().apply {
                arguments = Bundle().apply {
                    putString("chatName", chatName)
                    putString("authorName", authorName)
                    putInt("chatroomId", chatroomId)
                    putInt("artistId", artistId)
                    putString("artistProfileImage", artistProfileImage)
                    putInt("requestId", requestId)
                    putString("thumbnailUrl", thumbnailUrl)
                    putBoolean("isNewChat", false)
                }
            }
        }
        
        // 포스트에서 시작하는 채팅용 (기존 FragmentPostChatDetail 기능)
        fun newInstanceFromPost(
            chatName: String,
            authorName: String,
            commissionId: Int,
            hasSubmittedApplication: Boolean = false,
            chatroomId: Int = 1,
            sourceFragment: String = "CommissionForm",
            thumbnailUrl: String = "",
            artistId: Int = -1  // ✅ artistId 추가
        ): FragmentChatDetail {
            return FragmentChatDetail().apply {
                arguments = Bundle().apply {
                    putString("chatName", chatName)
                    putString("authorName", authorName)
                    putInt("commissionId", commissionId)
                    putBoolean("hasSubmittedApplication", hasSubmittedApplication)
                    putInt("chatroomId", chatroomId)
                    putString("sourceFragment", sourceFragment)
                    putString("thumbnailUrl", thumbnailUrl)
                    putInt("artistId", artistId)  // ✅ artistId 전달
                    putBoolean("isNewChat", true)
                }
            }
        }
    }
}