package com.example.commit.ui.chatlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.activity.MainActivity
import com.example.commit.viewmodel.ChatViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatDeleteFragment : Fragment() {

    private lateinit var chatViewModel: ChatViewModel
    private val selectedItems = mutableStateListOf<ChatItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val chatItemsState = remember { mutableStateOf<List<ChatItem>>(emptyList()) }
                    val rawRoomsState = remember { mutableStateOf<List<RetrofitClient.ChatroomItem>>(emptyList()) }
                    val isLoadingState = remember { mutableStateOf(true) }

                    // 채팅방 목록 로드
                    LaunchedEffect(Unit) {
                        fetchChatroomList { rooms ->
                            rawRoomsState.value = rooms
                            chatItemsState.value = rooms.map { room ->
                                ChatItem(
                                    id = room.chatroomId.toIntOrNull() ?: 0,
                                    profileImageRes = R.drawable.ic_profile,
                                    profileImageUrl = room.artistProfileImage,
                                    name = room.artistNickname,
                                    message = room.lastMessage ?: "커미션 작업 완료",
                                    time = room.lastMessageTime ?: "방금 전",
                                    isNew = room.hasUnread > 0,
                                    title = room.commissionTitle
                                )
                            }
                            isLoadingState.value = false
                        }
                    }

                    ChatDeleteScreen(
                        chatItems = chatItemsState.value,
                        selectedItems = selectedItems,
                        onItemToggle = {
                            if (selectedItems.contains(it)) selectedItems.remove(it)
                            else selectedItems.add(it)
                        },
                        onDeleteClick = {
                            // 선택된 채팅방들의 ID 추출
                            val chatroomIds = selectedItems.map { it.id }
                            if (chatroomIds.isNotEmpty()) {
                                // 실제 삭제 API 호출
                                chatViewModel.deleteChatrooms(
                                    context = requireContext(),
                                    chatroomIds = chatroomIds,
                                    onSuccess = {
                                        // 삭제 성공 시 화면 닫기
                                        parentFragmentManager.popBackStack()
                                    },
                                    onError = { errorMessage ->
                                        // 에러는 ViewModel에서 관리되므로 여기서는 로그만 출력
                                        Log.e("ChatDelete", "삭제 실패: $errorMessage")
                                    }
                                )
                            }
                        },
                        onBackClick = {
                            parentFragmentManager.popBackStack()
                        },
                        isDeleting = chatViewModel.isDeleting,
                        deleteError = chatViewModel.deleteError,
                        onClearError = {
                            chatViewModel.clearDeleteError()
                        }
                    )
                }
            }
        }
    }

    /**
     * 채팅방 목록 조회: FragmentChat과 동일한 로직
     */
    private fun fetchChatroomList(onSuccess: (List<RetrofitClient.ChatroomItem>) -> Unit) {
        Log.d("ChatDeleteAPI", "채팅방 목록 조회 시작")
        val api = RetrofitObject.getRetrofitService(requireContext())
        api.getChatroomList().enqueue(object :
            Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>
            ) {
                Log.d("ChatDeleteAPI", "응답 코드: ${response.code()}")
                if (!response.isSuccessful) {
                    Log.e("ChatDeleteAPI", "API 실패: ${response.code()} / ${response.errorBody()?.string()}")
                    onSuccess(emptyList())
                    return
                }
                val data = response.body()?.success.orEmpty()
                Log.d("ChatDeleteAPI", "채팅방 개수: ${data.size}")
                onSuccess(data)
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                t: Throwable
            ) {
                Log.e("ChatDeleteAPI", "네트워크 오류", t)
                onSuccess(emptyList())
            }
        })
    }
}
