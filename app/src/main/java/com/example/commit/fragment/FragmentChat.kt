package com.example.commit.fragment

import android.content.Context
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

class FragmentChat : Fragment() {

    // 채팅방 목록 새로고침 콜백
    private var refreshCallback: (() -> Unit)? = null
    

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.showBottomNav(true)
        refreshCallback?.invoke()
    }

    // 외부에서 새로고침 트리거
    fun refreshChatroomList() {
        refreshCallback?.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("FragmentChat", "onCreateView")
        return ComposeView(requireContext()).apply {
            setContent {
                CommitTheme {
                    val showBottomSheet = remember { mutableStateOf(false) }
                    val chatItemsState = remember { mutableStateOf<List<ChatItem>>(emptyList()) }
                    // 원본 API 데이터 저장 (id 매칭용)
                    val rawRoomsState = remember { mutableStateOf<List<RetrofitClient.ChatroomItem>>(emptyList()) }
                    val isLoadingState = remember { mutableStateOf(true) }

                    // 최초 로드
                    LaunchedEffect(Unit) {
                        Log.d("FragmentChat", "fetchChatroomList 최초 호출")
                        fetchChatroomList { rooms ->
                            rawRoomsState.value = rooms
                            chatItemsState.value = rooms.map { room ->
                                ChatItem(
                                    profileImageRes = R.drawable.ic_profile,
                                    profileImageUrl = room.artistProfileImage,
                                    name = room.artistNickname,
                                    message = room.lastMessage ?: "새로운 메시지가 없습니다",
                                    time = formatTime(room.lastMessageTime),
                                    isNew = room.hasUnread > 0,
                                    title = room.commissionTitle // 새 필드명

                                )
                            }
                            isLoadingState.value = false
                        }
                    }

                    // 새로고침 콜백
                    refreshCallback = {
                        Log.d("FragmentChat", "fetchChatroomList 새로고침")
                        isLoadingState.value = true
                        fetchChatroomList { rooms ->
                            rawRoomsState.value = rooms
                            chatItemsState.value = rooms.map { room ->
                                ChatItem(
                                    profileImageRes = R.drawable.ic_profile,
                                    profileImageUrl = room.artistProfileImage,
                                    name = room.artistNickname,
                                    message = room.lastMessage ?: "새로운 메시지가 없습니다",
                                    time = formatTime(room.lastMessageTime),
                                    isNew = room.hasUnread > 0,
                                    title = room.commissionTitle
                                )
                            }
                            isLoadingState.value = false
                        }
                    }

                    ChatListScreen(
                        chatItems = chatItemsState.value,
                        isLoading = isLoadingState.value,
                        onItemClick = { clickedItem ->
                            // 화면 아이템과 원본 룸 매칭(제목+닉네임 기준)
                            val room = rawRoomsState.value.firstOrNull {
                                it.commissionTitle == clickedItem.title &&
                                        it.artistNickname == clickedItem.name
                            }
                            if (room == null) {
                                Log.e("FragmentChat", "클릭 매칭 실패: ${clickedItem.title} / ${clickedItem.name}")
                                return@ChatListScreen
                            }

                            val chatroomId = room.chatroomId.toIntOrNull() ?: -1
                            val commissionId = room.commissionId.toIntOrNull() ?: -1
                            val artistId = room.artistId.toIntOrNull() ?: -1
                            val requestId    = room.requestId ?: -1
                            val thumbnailUrl = room.thumbnailUrl ?: ""
                            val profileUrl   = room.artistProfileImage

                            (activity as? MainActivity)?.showBottomNav(false)

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.Nav_Frame, FragmentPostChatDetail().apply {
                                    arguments = bundleOf(
                                        "chatName" to room.commissionTitle,
                                        "authorName" to room.artistNickname,
                                        "chatroomId" to chatroomId,
                                        "commissionId" to commissionId,
                                        "artistId" to artistId,
                                        "requestId" to requestId,
                                        "thumbnailUrl" to thumbnailUrl,
                                        "artistProfileImage" to profileUrl,
                                        "hasSubmittedApplication" to false,
                                    )
                                })
                                .addToBackStack("chatDetail")
                                .commit()
                        },
                        onSettingClick = { showBottomSheet.value = true }
                    )

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

    /**
     * 채팅방 목록 조회: 원본 Room 리스트를 콜백으로 반환
     */
    private fun fetchChatroomList(onSuccess: (List<RetrofitClient.ChatroomItem>) -> Unit) {
        // 토큰 로그(확인용)
        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("accessToken", "토큰 없음")
        Log.d("FragmentChat", "현재 저장된 토큰 길이: ${token?.length ?: 0}")

        Log.d("ChatAPI", "채팅방 목록 조회 시작")
        val api = RetrofitObject.getRetrofitService(requireContext())
        api.getChatroomList().enqueue(object :
            Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>
            ) {
                Log.d("ChatAPI", "응답 코드: ${response.code()}")
                if (!response.isSuccessful) {
                    Log.e("ChatAPI", "API 실패: ${response.code()} / ${response.errorBody()?.string()}")
                    onSuccess(emptyList())
                    return
                }
                val data = response.body()?.success.orEmpty()
                Log.d("ChatAPI", "채팅방 개수: ${data.size}")
                onSuccess(data)
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatroomItem>>>,
                t: Throwable
            ) {
                Log.e("ChatAPI", "네트워크 오류", t)
                onSuccess(emptyList())
            }
        })
    }

    private fun formatTime(timeString: String?): String {
        if (timeString.isNullOrEmpty()) return "방금 전"
        // TODO: ISO 8601 → 상대시간 등으로 변환
        return "방금 전"
    }

    /**
     * 새 스펙에 맞춘 채팅방 생성: userId는 JWT에서 추출(백엔드 처리)
     */
    fun createChatroom(
        artistId: Int,
        commissionId: Int,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        val api = RetrofitObject.getRetrofitService(requireContext())
        val request = RetrofitClient.CreateChatroomRequest(
            artistId = artistId,
            commissionId = commissionId
        )

        api.createChatroom(request).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                if (!response.isSuccessful) {
                    Log.e("ChatAPI", "채팅방 생성 실패: ${response.code()}")
                    onError("채팅방 생성에 실패했습니다")
                    return
                }
                val data = response.body()?.success
                if (data == null) {
                    Log.e("ChatAPI", "채팅방 생성 실패: success 데이터 없음")
                    onError("채팅방 생성에 실패했습니다")
                    return
                }
                val idInt = data.id.toIntOrNull()
                if (idInt == null) {
                    Log.e("ChatAPI", "채팅방 id 파싱 실패: ${data.id}")
                    onError("채팅방 생성에 실패했습니다")
                    return
                }
                Log.d("ChatAPI", "채팅방 생성 성공: ${data.id}")
                refreshChatroomList()
                onSuccess(idInt)
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