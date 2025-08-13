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
import android.content.Context

class FragmentChat : Fragment() {
    
    // 채팅방 목록 새로고침을 위한 콜백
    private var refreshCallback: (() -> Unit)? = null
    
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(true)
        // 화면이 다시 보여질 때 채팅방 목록 새로고침
        refreshCallback?.invoke()
    }
    
    // 외부에서 채팅방 목록 새로고침을 요청할 수 있는 메서드
    fun refreshChatroomList() {
        refreshCallback?.invoke()
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
                    
                    // 새로고침 콜백 설정
                    refreshCallback = {
                        Log.d("FragmentChat", "채팅방 목록 새로고침 시작")
                        isLoadingState.value = true
                        fetchChatroomList { chatItems ->
                            Log.d("FragmentChat", "새로고침 완료, 채팅방 개수: ${chatItems.size}")
                            chatItemsState.value = chatItems
                            isLoadingState.value = false
                        }
                    }

                    // ChatListScreen에 onSettingClick 인자를 넘김
                    ChatListScreen(
                        chatItems = chatItemsState.value,
                        isLoading = isLoadingState.value,
                        onItemClick = { clickedItem ->
                            // 실제 채팅방 ID를 찾아서 전달
                            val chatroomData = chatItemsState.value?.find { 
                                it.title == clickedItem.title && it.name == clickedItem.name 
                            }
                            val actualChatroomId = chatroomData?.let { 
                                // chatItems에서 원본 데이터 찾기 (추후 개선 필요)
                                1 // 임시로 1 사용, 실제로는 매핑 테이블 필요
                            } ?: 1
                            
                            // 채팅방 상세로 이동 시 BottomNavigation 숨기기
                            (activity as? MainActivity)?.showBottomNav(false)
                            
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.Nav_Frame, FragmentPostChatDetail().apply {
                                    arguments = bundleOf(
                                        "chatName" to clickedItem.title,     // 커미션 제목
                                        "authorName" to clickedItem.name,     // 작가 이름
                                        "chatroomId" to actualChatroomId,     // 실제 채팅방 ID
                                        "commissionId" to actualChatroomId,   // 커미션 ID (동일하게 설정)
                                        "hasSubmittedApplication" to false   // 신청서 제출 여부
                                    )
                                })
                                .addToBackStack("chatDetail")
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
        // 현재 저장된 토큰 상태 확인
        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", "토큰 없음")
        Log.d("FragmentChat", "현재 저장된 토큰: $token")
        Log.d("FragmentChat", "토큰 길이: ${token?.length ?: 0}")
        
        Log.d("ChatAPI", "채팅방 목록 조회 시작")
        val api = RetrofitObject.getRetrofitService(requireContext())
        Log.d("ChatAPI", "API 서비스 생성됨: $api")
        
        api.getChatroomList().enqueue(object : Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>
            ) {
                Log.d("ChatAPI", "API 응답 받음: ${response.code()}")
                Log.d("ChatAPI", "응답 바디: ${response.body()}")
                Log.d("ChatAPI", "응답 원시 데이터: ${response.raw()}")
                
                if (response.isSuccessful) {
                    try {
                        val data = response.body()?.success
                        if (data != null) {
                            Log.d("ChatAPI", "채팅방 개수: ${data.size}")
                            Log.d("ChatAPI", "채팅방 원시 데이터: $data")
                            val chatItems = data.mapNotNull { chatroom ->
                                try {
                                    Log.d("ChatAPI", "채팅방 데이터 확인: id=${chatroom.chatroomId}, artist=${chatroom.artistNickname}, request=${chatroom.requestTitle}")
                                    
                                    ChatItem(
                                        profileImageRes = R.drawable.ic_profile,
                                        name = chatroom.artistNickname,
                                        message = chatroom.lastMessage ?: "새로운 메시지가 없습니다",
                                        time = formatTime(chatroom.lastMessageTime),
                                        isNew = chatroom.hasUnread > 0,
                                        title = chatroom.requestTitle
                                    )
                                } catch (e: Exception) {
                                    Log.e("ChatAPI", "채팅방 데이터 매핑 실패: ${e.message}")
                                    null
                                }
                            }
                            Log.d("ChatAPI", "실제 API 데이터로 채팅방 목록 설정")
                            onSuccess(chatItems)
                        } else {
                            Log.e("ChatAPI", "success 데이터가 없음")
                            Log.e("ChatAPI", "전체 응답: ${response.body()}")
                            Log.e("ChatAPI", "응답 상세: resultType=${response.body()?.resultType}, error=${response.body()?.error}")
                            // 빈 목록 반환 (더미 데이터 사용하지 않음)
                            onSuccess(emptyList())
                        }
                    } catch (e: Exception) {
                        Log.e("ChatAPI", "API 응답 처리 중 오류: ${e.message}")
                        // 빈 목록 반환 (더미 데이터 사용하지 않음)
                        onSuccess(emptyList())
                    }
                } else {
                    Log.e("ChatAPI", "API 실패: ${response.code()}")
                    Log.e("ChatAPI", "에러 응답: ${response.errorBody()?.string()}")
                    // 빈 목록 반환 (더미 데이터 사용하지 않음)
                    onSuccess(emptyList())
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                t: Throwable
            ) {
                Log.e("ChatAPI", "네트워크 오류", t)
                Log.e("ChatAPI", "오류 메시지: ${t.message}")
                
                // 빈 목록 반환 (더미 데이터 사용하지 않음)
                onSuccess(emptyList())
            }
        })
    }

    private fun getDefaultChatItems(): List<ChatItem> {
        // 더미 데이터 대신 빈 목록 반환
        return emptyList()
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
                        // 채팅방 생성 성공 시 목록 새로고침
                        refreshChatroomList()
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


