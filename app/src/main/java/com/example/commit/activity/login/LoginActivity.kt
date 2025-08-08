package com.example.commit.activity.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.activity.MainActivity
import com.example.commit.databinding.ActivityLoginBinding
import androidx.browser.customtabs.CustomTabsIntent

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

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
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        Log.d("LoginActivity", "intent.action = ${intent.action}")
        Log.d("LoginActivity", "intent.data = ${intent.data}")
        Log.d("LoginActivity", "intent.extras = ${intent.extras}")

        val data: Uri? = intent.data
        Log.d("LoginActivity", "딥링크 수신: $data")

        val token = data?.getQueryParameter("token") ?: intent.getStringExtra("token")
        val signupRequired = data?.getQueryParameter("signupRequired")?.toBoolean()
            ?: intent.getBooleanExtra("signupRequired", false)

        if (!token.isNullOrEmpty()) {
            val prefs = getSharedPreferences("auth", MODE_PRIVATE)
            prefs.edit().putString("accessToken", token).apply()
            android.util.Log.d("LoginActivity", "저장된 토큰: $token")

            if (signupRequired) {
                val signUpIntent = Intent(this, AgreeFirstActivity::class.java).apply {
                    putExtra("token", token)
                }
                startActivity(signUpIntent)
            } else {
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(mainIntent)
            }
            finishAffinity()
        } else {
            Log.e("LoginActivity", "token이 null입니다. 딥링크 data=$data")
        }
    }
}