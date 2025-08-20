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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {
    // ───────────────────────────────────────────────────────────
    // 사용자/룸/입력 상태
    // ───────────────────────────────────────────────────────────
    var currentUserId: String by mutableStateOf("")
        private set

    fun loadCurrentUserId(context: Context) {
        currentUserId = RetrofitObject.getCurrentUserId(context) ?: ""
        myUserId = currentUserId.toIntOrNull()
    }

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

    private var hasLoadedDummyData = false

    fun onMessageChange(newMessage: String) { message = newMessage }

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
    // 로컬 저장 유틸 (SharedPreferences + Gson)
    // ───────────────────────────────────────────────────────────
    private val gson = Gson()
    private fun chatPrefs(ctx: Context) =
        ctx.getSharedPreferences("chat_store", Context.MODE_PRIVATE)
    private fun chatKey(roomId: Int) = "chat_messages_$roomId"

    /** 외부에서도 저장이 필요할 수 있어 public */
    fun saveMessages(ctx: Context) {
        val roomId = chatroomId
        if (roomId <= 0) return
        val json = gson.toJson(chatMessages)
        chatPrefs(ctx).edit().putString(chatKey(roomId), json).apply()
    }

    private fun loadLocalMessages(ctx: Context, roomId: Int): List<ChatMessage> {
        val json = chatPrefs(ctx).getString(chatKey(roomId), null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<ChatMessage>>() {}.type
            gson.fromJson<List<ChatMessage>>(json, type) ?: emptyList()
        } catch (_: Throwable) { emptyList() }
    }

    // ───────────────────────────────────────────────────────────
    // 더미 데이터 (API 실패/빈 방일 때만 폴백)
    // ───────────────────────────────────────────────────────────
    private fun loadDummyMessages() {
        if (hasLoadedDummyData) return
        val now = System.currentTimeMillis()
        chatMessages = listOf(
            ChatMessage(id = "1", senderId = "me",     content = "안녕하세요",             timestamp = now - 600000, type = MessageType.TEXT,                amount = null),
            ChatMessage(id = "2", senderId = "artist", content = "반가워요!",             timestamp = now - 590000, type = MessageType.TEXT,                amount = null),
            ChatMessage(id = "3", senderId = "me",     content = "25.06.02 17:50",        timestamp = now - 580000, type = MessageType.COMMISSION_REQUEST,  amount = null),
            ChatMessage(id = "4", senderId = "artist", content = "낙서 타입 커미션",       timestamp = now - 570000, type = MessageType.COMMISSION_ACCEPTED, amount = null),
            ChatMessage(id = "5", senderId = "artist", content = "",                      timestamp = now - 560000, type = MessageType.PAYMENT,             amount = 40000),
            ChatMessage(id = "6", senderId = "me",     content = "",                      timestamp = now - 550000, type = MessageType.PAYMENT_COMPLETE,    amount = null),
            ChatMessage(id = "7", senderId = "artist", content = "",                      timestamp = now - 540000, type = MessageType.COMMISSION_START,    amount = null),
            ChatMessage(id = "8", senderId = "artist", content = "25.06.02 17:50",        timestamp = now - 530000, type = MessageType.COMMISSION_COMPLETE, amount = null),
        )
        hasLoadedDummyData = true
        Log.d("ChatViewModel", "더미데이터 로드 완료: ${chatMessages.size}개")
    }

    // ───────────────────────────────────────────────────────────
    // 메시지 추가/전송
    // ───────────────────────────────────────────────────────────
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

    fun sendMessage() {
        if (message.isBlank()) return
        addNewMessage(message)
    }

    /** 전송 직후 로컬 저장 */
    fun sendMessage(context: Context) {
        sendMessage()
        saveMessages(context) // ★ 전송하면 바로 저장
    }

    // 임의 시스템 메시지(결제 등)도 저장하고 싶으면 호출부에서 saveMessages(context) 한 줄만 추가하세요.
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

    // ───────────────────────────────────────────────────────────
    // 로드(로컬 선반영 → 서버 머지 → 다시 저장)
    // ───────────────────────────────────────────────────────────
    fun loadMessages(context: Context, chatroomId: Int, limit: Int = 20) {
        // 1) 로컬 선반영
        val local = loadLocalMessages(context, chatroomId)
        if (local.isNotEmpty()) {
            chatMessages = local.takeLast(20)
            Log.d("ChatViewModel", "로컬 로드: ${local.size}개")
        }

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
                        // 로컬만 유지
                        return
                    }

                    val mapped = data.map { apiMsg ->
                        ChatMessage(
                            id = apiMsg.messageId.toString(),
                            senderId = if (myUserId != null && apiMsg.senderId == myUserId) "me" else "artist",
                            content = apiMsg.content,
                            timestamp = parseTimestamp(apiMsg.createdAt),
                            type = MessageType.TEXT, // 서버 타입 분기 생기면 매핑 확장
                            amount = null
                        )
                    }

                    // 로컬과 머지(중복 제거 → 시간순 → 최대 20개)
                    val merged = (chatMessages + mapped)
                        .distinctBy { it.id }
                        .sortedBy { it.timestamp }
                        .takeLast(20)

                    chatMessages = merged
                    currentOffset += data.size
                    saveMessages(context) // ★ 서버 로딩 후에도 저장(동기화)
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

    // ───────────────────────────────────────────────────────────
    // createdAt 파싱
    // ───────────────────────────────────────────────────────────
    private fun parseTimestamp(createdAt: String): Long {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try { return java.time.Instant.parse(createdAt).toEpochMilli() } catch (_: Throwable) {}
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
}
