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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
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
                            // ✅ 최신 메시지 시간 순으로 정렬 (최신이 위로)
                            val sortedRooms = rooms.sortedByDescending { room ->
                                room.lastMessageTime ?: "1970-01-01T00:00:00Z" // null인 경우 가장 오래된 시간으로 처리
                            }
                            chatItemsState.value = sortedRooms.map { room ->
                                val localLast = loadLocalLastMessage(requireContext(), room.chatroomId)
                                val preview = room.lastMessage
                                    ?: localLast?.let { formatPreview(it) }
                                    ?: "커미션 작업 완료"

                                ChatItem(
                                    profileImageRes = R.drawable.ic_profile,
                                    profileImageUrl = room.artistProfileImage,
                                    name = room.artistNickname,
                                    message = preview,                                // ★ 개선된 메시지
                                    time = pickTimeString(room.lastMessageTime, localLast), // ★ 개선된 시간
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
                            // ✅ 최신 메시지 시간 순으로 정렬 (최신이 위로)
                            val sortedRooms = rooms.sortedByDescending { room ->
                                room.lastMessageTime ?: "1970-01-01T00:00:00Z" // null인 경우 가장 오래된 시간으로 처리
                            }
                            chatItemsState.value = sortedRooms.map { room ->
                                val localLast = loadLocalLastMessage(requireContext(), room.chatroomId)
                                val preview = room.lastMessage
                                    ?: localLast?.let { formatPreview(it) }
                                    ?: "커미션 작업 완료"

                                ChatItem(
                                    profileImageRes = R.drawable.ic_profile,
                                    profileImageUrl = room.artistProfileImage,
                                    name = room.artistNickname,
                                    message = preview,                                // ★ 개선된 메시지
                                    time = pickTimeString(room.lastMessageTime, localLast), // ★ 개선된 시간
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
                                .replace(R.id.Nav_Frame, FragmentChatDetail.newInstanceFromPost(
                                    chatName = room.commissionTitle,
                                    authorName = room.artistNickname,
                                    chatroomId = chatroomId,
                                    commissionId = commissionId,
                                    hasSubmittedApplication = false,
                                    sourceFragment = "FragmentChat",
                                    thumbnailUrl = thumbnailUrl
                                ))
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
        try {
            val userId = com.example.commit.connection.RetrofitObject.getCurrentUserId(requireContext())
            Log.d("FragmentChat", "JWT userId: ${userId ?: "null"}")
        } catch (_: Exception) {
        }

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
    
    // 로컬에서 마지막 메시지 로드
    private fun loadLocalLastMessage(ctx: Context, chatroomId: String?): ChatMessage? {
        val id = chatroomId?.toIntOrNull() ?: return null
        val prefs = ctx.getSharedPreferences("chat_store", Context.MODE_PRIVATE)
        val key = "chat_messages_$id"
        val json = prefs.getString(key, null) ?: return null
        return try {
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            val list: List<ChatMessage> = Gson().fromJson(json, type) ?: emptyList()
            list.lastOrNull()
        } catch (_: Throwable) { null }
    }
    
    // 메시지 타입별 미리보기 텍스트 생성
    private fun formatPreview(msg: ChatMessage): String {
        return when (msg.type) {
            MessageType.TEXT -> msg.content.ifBlank { "메시지" }
            MessageType.COMMISSION_REQUEST -> "신청서 전송"
            MessageType.COMMISSION_ACCEPTED -> "커미션 수락"
            MessageType.PAYMENT -> "결제 요청 ${msg.amount ?: ""}"
            MessageType.PAYMENT_COMPLETE -> "결제 완료"
            MessageType.COMMISSION_START -> "작업 시작"
            MessageType.COMMISSION_COMPLETE -> "작업 완료"
            else -> "메시지"
        }
    }
    
    // 시간 우선순위 결정 (서버 → 로컬)
    private fun pickTimeString(roomTime: String?, local: ChatMessage?): String {
        return roomTime ?: run {
            if (local != null) "방금 전" else "방금 전"
        }
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
            commissionId = commissionId.toString()
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