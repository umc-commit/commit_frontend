package com.example.commit.activity.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.activity.MainActivity
import com.example.commit.databinding.ActivityLoginBinding
import androidx.browser.customtabs.CustomTabsIntent
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    @Volatile private var handledOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 구글 로그인 버튼 클릭 시
        binding.googleLogin.setOnClickListener {
            val googleLoginUrl = "https://commit.n-e.kr/api/users/oauth2/login/google"
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setInstantAppsEnabled(false)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(googleLoginUrl))
        }

        // 카카오 로그인 버튼 클릭 시
        binding.kakaoLoginButton.setOnClickListener {
            val kakaoLoginUrl = "https://commit.n-e.kr/api/users/oauth2/login/kakao"
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .setInstantAppsEnabled(false)
                .build()
            customTabsIntent.launchUrl(this, Uri.parse(kakaoLoginUrl))
        }

        // 앱이 딥링크로 호출된 경우 토큰 처리
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(i: Intent?) {
        if (handledOnce) return
        val data = i?.data
        val token = data?.getQueryParameter("token") ?: i?.getStringExtra("token")
        val signupRequired = data?.getQueryParameter("signupRequired")?.toBoolean()
            ?: i?.getBooleanExtra("signupRequired", false) ?: false

        if (token.isNullOrBlank()) {
            Log.e("LoginActivity", "token null. data=$data")
            return
        }
        handledOnce = true

        // 1) 토큰 저장
        getSharedPreferences("auth", MODE_PRIVATE)
            .edit().putString("accessToken", token).apply()

        // 저장된 토큰 로그 출력
        Log.d("LoginActivity", "저장된 accessToken = $token")

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcm ->
                Log.d("LoginActivity", "현재 FCM Token = $fcm")
            }
            .addOnFailureListener { e ->
                Log.w("LoginActivity", "Failed to get current FCM token", e)
            }

        // 2) 라우팅
        if (signupRequired) {
            startActivity(Intent(this, AgreeFirstActivity::class.java).apply {
                putExtra("token", token)
                // 필요 시 CLEAR_TOP 정도만
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            })
            finish() // LoginActivity만 종료
            return
        }

        // 3) 메인으로 (같은 태스크에서 전환)
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        })
        finish() // 여기서 finishAffinity() 쓰지 말 것!
    }
}