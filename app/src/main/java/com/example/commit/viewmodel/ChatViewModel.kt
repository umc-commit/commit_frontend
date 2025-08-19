package com.example.commit.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {
    var currentUserId: String by mutableStateOf("")
        private set

    fun loadCurrentUserId(context: Context) {
        currentUserId = RetrofitObject.getCurrentUserId(context) ?: ""
        myUserId = currentUserId.toIntOrNull()
    }

    // 로그인 유저 ID (Int). JWT 디코드 or /users/me로 받아서 setMyUserId로 주입
    var myUserId: Int? = null
        private set
    fun setMyUserId(id: Int) { myUserId = id }

    var chatroomId: Int = 0
        private set

    var message by mutableStateOf("")
        private set

    // 최대 20개 유지
    var chatMessages by mutableStateOf(listOf<ChatMessage>())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var currentOffset: Int = 0
        private set

    var hasSubmittedApplication: Boolean = false
        private set

    // 더미 사용 여부(폴백용)
    private var hasLoadedDummyData = false

    fun onMessageChange(newMessage: String) {
        message = newMessage
    }

    // 방 변경 시 상태 초기화 (이전: setChatroomId에서 더미 자동 로드 → 제거)
    fun setChatroomId(id: Int) {
        if (chatroomId == id) return
        chatroomId = id
        currentOffset = 0
        isLoading = false
        chatMessages = emptyList()
        hasLoadedDummyData = false
        Log.d("ChatViewModel", "채팅방 변경: id=$id, 상태 초기화 완료")
    }

    fun setApplicationStatus(hasSubmitted: Boolean) {
        hasSubmittedApplication = hasSubmitted
    }

    // ───────────────────────────────────────────────────────────
    // 더미 데이터 (API 실패/빈 방일 때만 폴백으로 사용)
    // ───────────────────────────────────────────────────────────
    private fun loadDummyMessages() {
        if (hasLoadedDummyData) return
        val now = System.currentTimeMillis()
        chatMessages = listOf(
            ChatMessage(id = "1", senderId = "me",     content = "안녕하세요",             timestamp = now - 600000, type = MessageType.TEXT,               amount = null),
            ChatMessage(id = "2", senderId = "artist", content = "반가워요!",             timestamp = now - 590000, type = MessageType.TEXT,               amount = null),
            ChatMessage(id = "3", senderId = "me",     content = "25.06.02 17:50",        timestamp = now - 580000, type = MessageType.COMMISSION_REQUEST, amount = null),
            ChatMessage(id = "4", senderId = "artist", content = "낙서 타입 커미션",       timestamp = now - 570000, type = MessageType.COMMISSION_ACCEPTED,amount = null),
            ChatMessage(id = "5", senderId = "artist", content = "",                      timestamp = now - 560000, type = MessageType.PAYMENT,            amount = 40000),
            ChatMessage(id = "6", senderId = "me",     content = "",                      timestamp = now - 550000, type = MessageType.PAYMENT_COMPLETE,   amount = null),
            ChatMessage(id = "7", senderId = "artist", content = "",                      timestamp = now - 540000, type = MessageType.COMMISSION_START,   amount = null),
            ChatMessage(id = "8", senderId = "artist", content = "25.06.02 17:50",        timestamp = now - 530000, type = MessageType.COMMISSION_COMPLETE,amount = null),
        )
        hasLoadedDummyData = true
        Log.d("ChatViewModel", "더미데이터 로드 완료: ${chatMessages.size}개")
    }

    // 새 메시지 추가: 내 메시지는 항상 "me"
    fun addNewMessage(content: String, type: MessageType = MessageType.TEXT, amount: Int? = null) {
        val newMessage = ChatMessage(
            id = "local_${System.currentTimeMillis()}",
            senderId = "me",
            content = content,
            timestamp = System.currentTimeMillis(),
            type = type,
            amount = amount
        )
        chatMessages = (chatMessages + newMessage).takeLast(20)
        message = ""

        if (type == MessageType.COMMISSION_REQUEST) addSystemResponse()
    }

    private fun addSystemResponse() {
        viewModelScope.launch {
            delay(1000)
            val systemMessage = ChatMessage(
                id = "sys_${System.currentTimeMillis()}",
                senderId = "artist",
                content = "네, 알겠습니다! 커미션을 수락했습니다.",
                timestamp = System.currentTimeMillis(),
                type = MessageType.COMMISSION_ACCEPTED,
                amount = null
            )
            chatMessages = (chatMessages + systemMessage).takeLast(20)
        }
    }

    // 채팅 메시지 로드 (페이징)
    fun loadMessages(context: Context, chatroomId: Int, limit: Int = 20) {
        if (isLoading) return
        isLoading = true

        val api = RetrofitObject.getRetrofitService(context)
        api.getChatroomMessages(chatroomId, limit, currentOffset)
            .enqueue(object : Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>,
                    response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>
                ) {
                    isLoading = false
                    if (!response.isSuccessful) {
                        Log.e("ChatViewModel", "API 실패: ${response.code()}")
                        if (chatMessages.isEmpty()) loadDummyMessages()
                        return
                    }

                    val data = response.body()?.success.orEmpty()
                    if (data.isEmpty()) {
                        Log.d("ChatViewModel", "빈 채팅 or 더 이상 없음")
                        if (chatMessages.isEmpty()) loadDummyMessages()
                        return
                    }

                    // 서버 → 화면 모델 매핑
                    val mapped = data.map { apiMsg ->
                        ChatMessage(
                            id = apiMsg.messageId.toString(),
                            // 내/상대 구분을 정확히: senderId(Int)와 myUserId(Int?) 비교
                            senderId = if (myUserId != null && apiMsg.senderId == myUserId) "me" else "artist",
                            content = apiMsg.content,
                            timestamp = parseTimestamp(apiMsg.createdAt),
                            type = MessageType.TEXT, // 서버 타입 분기 생기면 여기서 매핑
                            amount = null
                        )
                    }

                    // 중복 제거 + 시간순 정렬(오래된 → 최신) + 최대 20개 유지
                    val merged = (chatMessages + mapped)
                        .distinctBy { it.id }
                        .sortedBy { it.timestamp }
                        .takeLast(20)

                    chatMessages = merged
                    currentOffset += data.size
                    Log.d("ChatViewModel", "로드 성공: +${data.size}, total=${chatMessages.size}, offset=$currentOffset")
                }

                override fun onFailure(
                    call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>,
                    t: Throwable
                ) {
                    isLoading = false
                    Log.e("ChatViewModel", "네트워크 오류", t)
                    if (chatMessages.isEmpty()) loadDummyMessages()
                }
            })
    }

    // createdAt 파싱 (우선 Instant.parse, 폴백은 UTC 지정)
    private fun parseTimestamp(createdAt: String): Long {
        // ISO 8601 with Z(UTC) 문자를 정상 처리
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                return java.time.Instant.parse(createdAt).toEpochMilli()
            } catch (_: Throwable) { /* fallback */ }
        }
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(createdAt)?.time ?: System.currentTimeMillis()
        } catch (_: Throwable) {
            try {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.parse(createdAt)?.time ?: System.currentTimeMillis()
            } catch (_: Throwable) {
                System.currentTimeMillis()
            }
        }
    }

    fun sendMessage() {
        if (message.isBlank()) return
        addNewMessage(message)
    }
    fun sendMessage(context: Context) { sendMessage() }

    // 더미 랜덤 응답 (옵션)
//    fun generateDummyResponse() {
//        viewModelScope.launch {
//            delay(1000L + (Math.random() * 2000).toLong())
//            val responses = listOf(
//                "네, 알겠습니다!", "좋은 아이디어네요 😊", "빠르게 작업해드릴게요!",
//                "감사합니다!", "곧 시작하겠습니다", "좋은 작품으로 보답하겠습니다",
//                "자세히 설명해주셔서 감사합니다", "꼼꼼하게 작업하겠습니다",
//                "맞습니다, 그렇게 하면 좋겠네요", "좋은 피드백 감사합니다!"
//            )
//            val dummyMessage = ChatMessage(
//                id = "dummy_${System.currentTimeMillis()}",
//                senderId = "artist",
//                content = responses.random(),
//                timestamp = System.currentTimeMillis(),
//                type = MessageType.TEXT,
//                amount = null
//            )
//            chatMessages = (chatMessages + dummyMessage).takeLast(20)
//        }
//    }

    fun showCommissionAcceptedMessage() {
        val m = ChatMessage(
            id = "commission_accepted_${System.currentTimeMillis()}",
            senderId = "artist",
            content = "커미션을 수락했습니다",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_ACCEPTED,
            amount = null
        )
        chatMessages = (chatMessages + m).takeLast(20)
    }

    fun showPaymentRequestMessage(amount: Int) {
        val m = ChatMessage(
            id = "payment_request_${System.currentTimeMillis()}",
            senderId = "artist",
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.PAYMENT,
            amount = amount
        )
        chatMessages = (chatMessages + m).takeLast(20)
    }

    fun clearChat() {
        chatMessages = emptyList()
        hasLoadedDummyData = false
        message = ""
        currentOffset = 0
        isLoading = false
    }
}