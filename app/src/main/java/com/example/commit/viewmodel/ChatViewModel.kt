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

    fun onMessageChange(newMessage: String) {
        message = newMessage
    }

    fun setChatroomId(id: Int) {
        chatroomId = id
    }
    
    fun setApplicationStatus(hasSubmitted: Boolean) {
        hasSubmittedApplication = hasSubmitted
    }
    


    // 채팅방 메시지 로드
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
                                timestamp = System.currentTimeMillis(), // TODO: Parse createdAt from apiMessage.createdAt
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
                        Log.d("ChatViewModel", "메시지 로드 성공 - ${data.size}개 메시지")
                    } else {
                        Log.d("ChatViewModel", "메시지 로드 완료 - 빈 채팅방")
                    }
                } else {
                    Log.e("ChatViewModel", "메시지 로드 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ApiResponse<List<RetrofitClient.ChatMessage>>>, t: Throwable) {
                isLoading = false
                Log.e("ChatViewModel", "메시지 로드 네트워크 오류", t)
            }
        })
    }

    // 메시지 전송 (전송 API 없음 - 로컬 저장만)
    fun sendMessage(context: Context) {
        if (message.isBlank()) return

        val userMessage = message
        val newUserChat = ChatMessage(
            id = System.currentTimeMillis().toString(),
            senderId = currentUserId,
            content = userMessage,
            timestamp = System.currentTimeMillis(),
            type = MessageType.TEXT,
            amount = null
        )

        // 사용자 메시지 즉시 추가 (로컬에만 저장)
        val updatedMessages = (chatMessages + newUserChat).takeLast(20)
        chatMessages = updatedMessages
        message = ""

        // TODO: 메시지 전송 API가 준비되면 여기에 추가
        Log.d("ChatViewModel", "메시지 전송 (로컬 저장): $userMessage")
        
        // 신청서 미제출 상태에서 첫 번째 텍스트 메시지인 경우 일반 대화 시작
        if (!hasSubmittedApplication && chatMessages.size == 1) {
            Log.d("ChatViewModel", "일반 대화 시작 - 시스템 메시지 시퀀스 없음")
            // 일반 대화는 시스템 메시지 없이 진행
        }
    }

    // 시스템 메시지들을 순차적으로 생성
    private fun generateSystemMessagesSequence() {
        viewModelScope.launch {
            val systemMessages = listOf(
                ChatMessage("system_1", "artist", "네, 알겠습니다!", System.currentTimeMillis(), MessageType.TEXT, null),
                ChatMessage("system_2", "artist", "커미션을 수락했습니다", System.currentTimeMillis(), MessageType.COMMISSION_ACCEPTED, null),
                ChatMessage("system_3", "artist", "", System.currentTimeMillis(), MessageType.PAYMENT, 50000),
                ChatMessage("system_4", "artist", "작업을 시작했습니다", System.currentTimeMillis(), MessageType.COMMISSION_START, null)
            )
            
            systemMessages.forEachIndexed { index, systemMessage ->
                delay(2000L + (index * 1500L)) // 2초 후부터 1.5초 간격으로 메시지 추가
                val updatedMessages = (chatMessages + systemMessage).takeLast(20)
                chatMessages = updatedMessages
            }
            
            // 마지막에 완료 메시지 (5초 후)
            delay(5000L)
            val completeMessage = ChatMessage(
                "system_complete",
                "artist",
                "작업이 완료되었습니다!",
                System.currentTimeMillis(),
                MessageType.COMMISSION_COMPLETE,
                null
            )
            chatMessages = (chatMessages + completeMessage).takeLast(20)
        }
    }

    // 더미 상대방 응답 생성 (실시간 채팅 느낌)
    fun generateDummyResponse() {
        val responses = listOf(
            "네, 좋습니다!",
            "알겠습니다 😊",
            "빠르게 작업해드릴게요!",
            "감사합니다!",
            "곧 시작하겠습니다",
            "좋은 작품으로 보답하겠습니다"
        )
        
        viewModelScope.launch {
            delay(1000L + (Math.random() * 2000).toLong()) // 1-3초 랜덤 딜레이
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

    // 채팅방 초기화 (빈 상태로 시작)
    fun initializeChatroom(chatroomId: Int, hasSubmittedApplication: Boolean = false) {
        this.chatroomId = chatroomId
        this.hasSubmittedApplication = hasSubmittedApplication
        chatMessages = emptyList()
        currentOffset = 0
        isLoading = false
        message = ""
        
        Log.d("ChatViewModel", "채팅방 초기화 - ID: $chatroomId, 신청서 제출됨: $hasSubmittedApplication")
        
        // 신청서 제출한 경우 즉시 CommissionRequestBubble 표시
        if (hasSubmittedApplication) {
            Log.d("ChatViewModel", "신청서 제출 상태 - CommissionRequestBubble 표시")
            showCommissionRequestBubble()
        } else {
            Log.d("ChatViewModel", "신청서 미제출 상태 - 빈 채팅방으로 시작")
        }
    }
    
    // CommissionRequestBubble 표시
    private fun showCommissionRequestBubble() {
        val requestBubbleMessage = ChatMessage(
            id = "commission_request_${System.currentTimeMillis()}",
            senderId = "system",
            content = "신청서가 제출되었습니다",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_REQUEST,
            amount = null
        )
        chatMessages = listOf(requestBubbleMessage)
        Log.d("ChatViewModel", "CommissionRequestBubble 추가됨")
    }
    
    // 커미션 수락 시스템 메시지 (상대방이 수락했을 때)
    fun showCommissionAcceptedMessage() {
        viewModelScope.launch {
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
    }
    
    // 결제 요청 시스템 메시지 (상대방이 결제 요청했을 때)
    fun showPaymentRequestMessage(amount: Int) {
        viewModelScope.launch {
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
    }
    
    // 결제 완료 시스템 메시지 (내가 결제했을 때)
    fun showPaymentCompleteMessage() {
        viewModelScope.launch {
            val paymentCompleteMessage = ChatMessage(
                id = "payment_complete_${System.currentTimeMillis()}",
                senderId = "me",
                content = "",
                timestamp = System.currentTimeMillis(),
                type = MessageType.PAYMENT_COMPLETE,
                amount = null
            )
            chatMessages = (chatMessages + paymentCompleteMessage).takeLast(20)
            
            // 결제 완료 후 작업 시작 메시지
            delay(2000L)
            showWorkStartMessage()
        }
    }
    
    // 작업 시작 시스템 메시지
    private fun showWorkStartMessage() {
        val workStartMessage = ChatMessage(
            id = "work_start_${System.currentTimeMillis()}",
            senderId = "artist",
            content = "",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_START,
            amount = null
        )
        chatMessages = (chatMessages + workStartMessage).takeLast(20)
        
        // 작업 시작 후 작업물 확인하기 메시지 (일정 시간 후)
        viewModelScope.launch {
            delay(5000L) // 5초 후
            showWorkCompleteMessage()
        }
    }
    
    // 작업물 확인하기 시스템 메시지
    private fun showWorkCompleteMessage() {
        val workCompleteMessage = ChatMessage(
            id = "work_complete_${System.currentTimeMillis()}",
            senderId = "artist",
            content = "작업물 확인하기",
            timestamp = System.currentTimeMillis(),
            type = MessageType.COMMISSION_COMPLETE,
            amount = null
        )
        chatMessages = (chatMessages + workCompleteMessage).takeLast(20)
    }

    override fun onCleared() {
        super.onCleared()
        // ViewModel 정리 시 메모리 해제
        chatMessages = emptyList()
        message = ""
    }
}