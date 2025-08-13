package com.example.commit.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.commit.data.model.ChatMessage
import com.example.commit.data.model.MessageType
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatViewModel : ViewModel() {

    val currentUserId = "me"
    var chatroomId: Int = 0
        private set

    var message by mutableStateOf("")
        private set

    // 채팅 메시지 리스트 - 최대 20개로 제한
    var chatMessages by mutableStateOf(listOf<ChatMessage>())
        private set
        
    var isLoading by mutableStateOf(false)
        private set

    var currentOffset: Int = 0
        private set
        
    var hasSubmittedApplication: Boolean = false
        private set

    // 더미데이터를 위한 플래그들
    private var hasLoadedDummyData = false

    fun onMessageChange(newMessage: String) {
        message = newMessage
    }

    fun setChatroomId(id: Int) {
        chatroomId = id
        // 채팅방 ID가 설정되면 더미데이터 로드
        if (!hasLoadedDummyData) {
            loadDummyMessages()
        }
    }
    
    fun setApplicationStatus(hasSubmitted: Boolean) {
        hasSubmittedApplication = hasSubmitted
    }

    // 더미데이터를 활용한 메시지 로드 (MessageWithTimestamp와 ChatBubble 기반)
    private fun loadDummyMessages() {
        if (hasLoadedDummyData) return
        
        val dummyMessages = createDummyMessages()
        chatMessages = dummyMessages
        hasLoadedDummyData = true
        
        Log.d("ChatViewModel", "더미데이터 로드 완료: ${dummyMessages.size}개 메시지")
    }

    // MessageWithTimestamp와 ChatBubble에 구현된 더미데이터 생성
    private fun createDummyMessages(): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()
        
        // 1. 안녕하세요 메시지
        messages.add(ChatMessage(
            id = "1",
            senderId = "me",
            content = "안녕하세요",
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            amount = null
        ))
        
        // 2. 반가워요! 메시지
        messages.add(ChatMessage(
            id = "2",
            senderId = "artist",
            content = "반가워요!",
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            amount = null
        ))
        
        // 3. 커미션 신청 메시지
        messages.add(ChatMessage(
            id = "3",
            senderId = "me",
            content = "25.06.02 17:50",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_REQUEST,
            amount = null
        ))
        
        // 4. 커미션 수락 메시지
        messages.add(ChatMessage(
            id = "4",
            senderId = "artist",
            content = "낙서 타입 커미션",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_ACCEPTED,
            amount = null
        ))
        
        // 5. 결제 요청 메시지
        messages.add(ChatMessage(
            id = "5",
            senderId = "artist",
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.PAYMENT,
            amount = 40000
        ))
        
        // 6. 결제 완료 메시지
        messages.add(ChatMessage(
            id = "6",
            senderId = "me",
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.PAYMENT_COMPLETE,
            amount = null
        ))
        
        // 7. 작업 시작 메시지
        messages.add(ChatMessage(
            id = "7",
            senderId = "artist",
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_START,
            amount = null
        ))
        
        // 8. 작업 완료 메시지
        messages.add(ChatMessage(
            id = "8",
            senderId = "artist",
            content = "25.06.02 17:50",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_COMPLETE,
            amount = null
        ))
        
        return messages
    }

    // 새로운 메시지 추가 (더미데이터 기반)
    fun addNewMessage(content: String, type: MessageType = MessageType.TEXT, amount: Int? = null) {
        val newMessage = ChatMessage(
            id = (chatMessages.size + 1).toString(),
            senderId = currentUserId,
            content = content,
            timestamp = System.currentTimeMillis(),
            type = type,
            amount = amount
        )
        
        chatMessages = (chatMessages + newMessage).takeLast(20)
        message = "" // 입력 필드 초기화
        
        // 시스템 응답 메시지 자동 생성 (더미데이터 기반)
        if (type == MessageType.COMMISSION_REQUEST) {
            addSystemResponse()
        }
    }

    // 시스템 응답 메시지 자동 생성
    private fun addSystemResponse() {
        viewModelScope.launch {
            delay(1000) // 1초 후 응답
            
            val systemMessage = ChatMessage(
                id = (chatMessages.size + 1).toString(),
                senderId = "artist",
                content = "네, 알겠습니다! 커미션을 수락했습니다.",
                timestamp = System.currentTimeMillis(),
                type = MessageType.COMMISSION_ACCEPTED,
                amount = null
            )
            
            chatMessages = (chatMessages + systemMessage).takeLast(20)
        }
    }

    // 채팅방 메시지 로드 (API 기반)
    fun loadMessages(context: Context, chatroomId: Int, limit: Int = 20) {
        if (isLoading) return
        
        isLoading = true
        val api = RetrofitObject.getRetrofitService(context)
        
        api.getChatroomMessages(chatroomId, limit, currentOffset).enqueue(object : Callback<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>,
                response: Response<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>
            ) {
                isLoading = false
                if (response.isSuccessful) {
                    val data = response.body()?.success
                    if (data != null && data.isNotEmpty()) {
                        val newMessages = data.map { apiMessage ->
                            ChatMessage(
                                id = apiMessage.messageId.toString(),
                                senderId = if (apiMessage.senderId.toString() == currentUserId) "me" else "artist",
                                content = apiMessage.content,
                                timestamp = parseTimestamp(apiMessage.createdAt),
                                type = MessageType.TEXT,
                                amount = null
                            )
                        }
                        
                        // 기존 메시지와 합치기 (중복 제거)
                        val existingIds = chatMessages.map { it.id }.toSet()
                        val filteredMessages = newMessages.filter { it.id !in existingIds }
                        chatMessages = (filteredMessages + chatMessages).takeLast(20)
                        
                        // 다음 페이지를 위한 offset 업데이트
                        currentOffset += data.size
                        Log.d("ChatViewModel", "API 메시지 로드 성공 - ${data.size}개 메시지")
                    } else {
                        Log.d("ChatViewModel", "API 메시지 로드 완료 - 빈 채팅방")
                    }
                } else {
                    Log.e("ChatViewModel", "API 메시지 로드 실패: ${response.code()}")
                    // API 실패 시 더미데이터 사용
                    if (chatMessages.isEmpty()) {
                        loadDummyMessages()
                    }
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>, t: Throwable) {
                isLoading = false
                Log.e("ChatViewModel", "API 메시지 로드 네트워크 오류", t)
                // 네트워크 오류 시 더미데이터 사용
                if (chatMessages.isEmpty()) {
                    loadDummyMessages()
                }
            }
        })
    }

    // API 응답의 createdAt을 파싱하는 함수
    private fun parseTimestamp(createdAt: String): Long {
        return try {
            // ISO 8601 형식 파싱 시도
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.parse(createdAt)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            try {
                // 일반 날짜 형식 파싱 시도
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                format.parse(createdAt)?.time ?: System.currentTimeMillis()
            } catch (e2: Exception) {
                System.currentTimeMillis()
            }
        }
    }

    // 메시지 전송 (더미데이터 기반)
    fun sendMessage() {
        if (message.isBlank()) return
        
        addNewMessage(message)
    }

    // 메시지 전송 (Context 포함 - 기존 호환성)
    fun sendMessage(context: Context) {
        sendMessage()
    }

    // 더미 응답 생성
    fun generateDummyResponse() {
        viewModelScope.launch {
            delay(1000L + (Math.random() * 2000).toLong()) // 1-3초 랜덤 딜레이
            
            val responses = listOf(
                "네, 알겠습니다!",
                "좋은 아이디어네요 😊",
                "빠르게 작업해드릴게요!",
                "감사합니다!",
                "곧 시작하겠습니다",
                "좋은 작품으로 보답하겠습니다",
                "자세히 설명해주셔서 감사합니다",
                "꼼꼼하게 작업하겠습니다",
                "맞습니다, 그렇게 하면 좋겠네요",
                "좋은 피드백 감사합니다!"
            )
            
            val randomResponse = responses.random()
            val dummyMessage = ChatMessage(
                id = "dummy_${System.currentTimeMillis()}",
                senderId = "artist",
                content = randomResponse,
                timestamp = System.currentTimeMillis(),
                type = MessageType.TEXT,
                amount = null
            )
            
            chatMessages = (chatMessages + dummyMessage).takeLast(20)
        }
    }

    // 커미션 수락 메시지 표시
    fun showCommissionAcceptedMessage() {
        val acceptedMessage = ChatMessage(
            id = "commission_accepted_${System.currentTimeMillis()}",
            senderId = "artist",
            content = "커미션을 수락했습니다",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_ACCEPTED,
            amount = null
        )
        chatMessages = (chatMessages + acceptedMessage).takeLast(20)
    }

    // 결제 요청 메시지 표시
    fun showPaymentRequestMessage(amount: Int) {
        val paymentMessage = ChatMessage(
            id = "payment_request_${System.currentTimeMillis()}",
            senderId = "artist", 
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.PAYMENT,
            amount = amount
        )
        chatMessages = (chatMessages + paymentMessage).takeLast(20)
    }

    // 채팅방 초기화
    fun clearChat() {
        chatMessages = emptyList()
        hasLoadedDummyData = false
        message = ""
    }
}