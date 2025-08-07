package com.example.commit.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import kotlinx.coroutines.delay
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.entities.ChatItem
import com.example.commit.ui.Theme.CommitTheme
import com.example.commit.ui.chatlist.ChatDeleteFragment
import com.example.commit.ui.chatlist.ChatListScreen
import com.example.commit.ui.chatlist.DeleteOptionBottomSheet
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentChat : Fragment() {
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("FragmentChat", "FragmentChat onCreateView 시작")
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    val chatItemsState = remember { mutableStateOf<List<ChatItem>>(emptyList()) }
                    val isLoadingState = remember { mutableStateOf(true) }

                    // LaunchedEffect를 사용하여 한 번만 API 호출
                    LaunchedEffect(Unit) {
                        Log.d("FragmentChat", "LaunchedEffect 실행됨")
                        fetchChatroomList { chatItems ->
                            Log.d("FragmentChat", "API 호출 완료, 채팅방 개수: ${chatItems.size}")
                            chatItemsState.value = chatItems
                            isLoadingState.value = false
                        }
                    }

                    // 테스트용 채팅방 생성 버튼 (나중에 제거)
                    LaunchedEffect(Unit) {
                        // 3초 후에 테스트 채팅방 생성
                        kotlinx.coroutines.delay(3000)
                        createChatroom(
                            consumerId = 1,
                            artistId = 2,
                            requestId = 3,
                            onSuccess = { chatroomId ->
                                Log.d("ChatAPI", "테스트 채팅방 생성 성공: $chatroomId")
                                // 채팅방 생성 후 목록 새로고침
                                fetchChatroomList { chatItems ->
                                    chatItemsState.value = chatItems
                                }
                            },
                            onError = { error ->
                                Log.e("ChatAPI", "테스트 채팅방 생성 실패: $error")
                            }
                        )
                    }

                    // ChatListScreen에 onSettingClick 인자를 넘김
                    ChatListScreen(
                        chatItems = chatItemsState.value,
                        isLoading = isLoadingState.value,
                        onItemClick = { clickedItem ->
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.Nav_Frame, FragmentChatDetail().apply {
                                    arguments = bundleOf(
                                        "chatName" to clickedItem.title,     // 커미션 제목
                                        "authorName" to clickedItem.name       // 작가 이름
                                    )
                                })
                                .addToBackStack(null)
                                .commit()
                        },
                        onSettingClick = {
                            showBottomSheet.value = true
                        }
                    )

                    // 바텀시트 조건부 표시
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

    private fun fetchChatroomList(onSuccess: (List<ChatItem>) -> Unit) {
        Log.d("ChatAPI", "채팅방 목록 조회 시작")
        val api = RetrofitObject.getRetrofitService(requireContext())
        
        // 먼저 raw response를 확인하기 위해 동기 호출
        try {
            val response = api.getChatroomList().execute()
            Log.d("ChatAPI", "Raw response code: ${response.code()}")
            Log.d("ChatAPI", "Raw response body: ${response.body()}")
        } catch (e: Exception) {
            Log.e("ChatAPI", "Raw response 확인 실패: ${e.message}")
        }
        
        api.getChatroomList().enqueue(object : Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>
            ) {
                Log.d("ChatAPI", "API 응답 받음: ${response.code()}")
                Log.d("ChatAPI", "응답 바디: ${response.body()}")
                
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("ChatAPI", "채팅방 개수: ${data.size}")
                        val chatItems = data.map { chatroom ->
                            ChatItem(
                                profileImageRes = R.drawable.ic_profile,
                                name = chatroom.artist.nickname,
                                message = chatroom.lastMessage ?: "새로운 메시지가 없습니다",
                                time = formatTime(chatroom.lastMessageTime),
                                isNew = chatroom.unreadCount > 0,
                                title = chatroom.request.title
                            )
                        }
                        Log.d("ChatAPI", "실제 API 데이터로 채팅방 목록 설정")
                        onSuccess(chatItems)
                    } else {
                        Log.e("ChatAPI", "success 데이터가 없음")
                        Log.e("ChatAPI", "전체 응답: ${response.body()}")
                        // 기본 데이터로 폴백
                        onSuccess(getDefaultChatItems())
                    }
                } else {
                    Log.e("ChatAPI", "API 실패: ${response.code()}")
                    Log.e("ChatAPI", "에러 응답: ${response.errorBody()?.string()}")
                    // 기본 데이터로 폴백
                    onSuccess(getDefaultChatItems())
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                t: Throwable
            ) {
                Log.e("ChatAPI", "네트워크 오류", t)
                Log.e("ChatAPI", "오류 메시지: ${t.message}")
                
                // 실제 응답을 확인하기 위해 raw response 로깅
                try {
                    val response = call.execute()
                    Log.e("ChatAPI", "Raw response: ${response.body()}")
                } catch (e: Exception) {
                    Log.e("ChatAPI", "Raw response 확인 실패: ${e.message}")
                }
                
                // 기본 데이터로 폴백
                onSuccess(getDefaultChatItems())
            }
        })
    }

    private fun getDefaultChatItems(): List<ChatItem> {
        return listOf(
            ChatItem(R.drawable.ic_profile, "키르", "[결제 요청] 낙서 타임 커미션", "방금 전", true, "낙서 타입 커미션"),
            ChatItem(R.drawable.ic_profile, "브로콜리", "[커미션 완료] 일러스트 타입", "2일 전", false, "일러스트 타입 커미션")
        )
    }

    private fun formatTime(timeString: String?): String {
        if (timeString.isNullOrEmpty()) return "방금 전"
        // TODO: 실제 시간 포맷팅 로직 구현
        return "방금 전"
    }

    // 채팅방 생성 함수
    fun createChatroom(consumerId: Int, artistId: Int, requestId: Int, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        val api = RetrofitObject.getRetrofitService(requireContext())
        val request = RetrofitClient.CreateChatroomRequest(
            consumerId = consumerId,
            artistId = artistId,
            requestId = requestId
        )

        api.createChatroom(request).enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null) {
                        Log.d("ChatAPI", "채팅방 생성 성공: ${data.id}")
                        onSuccess(data.id)
                    } else {
                        Log.e("ChatAPI", "채팅방 생성 실패: success 데이터가 없음")
                        onError("채팅방 생성에 실패했습니다")
                    }
                } else {
                    Log.e("ChatAPI", "채팅방 생성 실패: ${response.code()}")
                    onError("채팅방 생성에 실패했습니다")
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                Log.e("ChatAPI", "채팅방 생성 네트워크 오류", t)
                onError("네트워크 오류가 발생했습니다")
            }
        })
    }
}


