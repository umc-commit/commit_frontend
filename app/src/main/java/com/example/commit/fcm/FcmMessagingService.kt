package com.example.commit.fcm

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FcmMessagingService : FirebaseMessagingService() {

    // 새 토큰 수신 시 서버 등록
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMToken", "FCMToken = $token")

        val prefs = applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val api = RetrofitObject.getRetrofitService(applicationContext)
        val body = RetrofitClient.FcmTokenRegisterRequest(fcmToken = token)
        api.registerFcmToken(body).enqueue(object : Callback<
                RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>
                > {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>
            ) {
                if (response.isSuccessful && response.body()?.success != null) {
                    prefs.edit().putString("fcmToken", token).apply()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>,
                t: Throwable
            ) { /* 로깅 정도만 */ }
        })
    }

    // 메시지 수신: foreground일 때는 우리가 표시, background일 때는 시스템 표시 그대로 사용
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // 서버는 notification + data를 함께 보냄
        val nTitle = message.notification?.title
        val nBody  = message.notification?.body
        val data   = message.data.toMutableMap()  // 문자열 맵

        val title = data["title"] ?: nTitle ?: "알림"
        val body  = data["content"] ?: data["message"] ?: nBody ?: ""

        // ---- 라우팅 규칙(서버가 내려주는 type 기준) ----
        // MainActivity는 "openFragment" 인텐트 키로 분기 처리함.
        //  - "postChatDetail" : chatroomId, chatName 등 필요
        //  - "chat"           : 채팅 목록
        when (data["type"]) {
            "chat_message" -> {
                data["openFragment"] = "postChatDetail"
                // 숫자형도 모두 문자열로 넘어오므로 MainActivity에서 Int 변환
                // (chatroomId, chatName, authorName, commissionId 등 그대로 extras로 전달)
            }
            "chat_list" -> {
                data["openFragment"] = "chat"
            }
            // 기타 타입은 openFragment 없이 기본 홈으로 진입
        }

        val isForeground = isAppInForeground()

        if (isForeground) {
            // 포그라운드: 우리가 직접 헤드업 알림 표시 (클릭 시 MainActivity로 data 전달)
            NotificationHelper.show(
                context = applicationContext,
                title = title,
                body  = body,
                extras = data
            )
        } else {
            // 백그라운드/종료: 서버의 notification을 시스템이 표시하므로 중복 표시 안 함.
            // 클릭 시 시스템이 런처를 열고, intent.extras에 data가 포함되어 MainActivity에서 처리됨.
            Log.d("FCM", "Background delivery (system notification). data=$data")
        }
    }

    // 앱 포그라운드 여부 (deprecated API지만 서비스 내 간단 체크용으로 충분)
    private fun isAppInForeground(): Boolean {
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val pkg = packageName
        val procs = am.runningAppProcesses ?: return false
        for (p in procs) {
            if (p.processName == pkg) {
                return p.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                        p.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE
            }
        }
        return false
    }
}