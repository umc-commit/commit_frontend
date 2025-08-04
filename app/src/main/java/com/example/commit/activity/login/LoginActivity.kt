package com.example.commit.activity.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

        // 앱이 딥링크로 호출된 경우 토큰 처리
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.scheme == "commit") { // manifest에 등록한 scheme과 동일해야 함
            val token = data.getQueryParameter("token")
            val signupRequired = data.getQueryParameter("signupRequired")?.toBoolean() ?: false

            if (!token.isNullOrEmpty()) {
                // 토큰 저장
                val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                prefs.edit().putString("accessToken", token).apply()

                // 저장된 토큰 확인 (로그 출력)
                val savedToken = prefs.getString("accessToken", null)
                android.util.Log.d("LoginActivity", "저장된 토큰: $savedToken")

                if (signupRequired) {
                    // 회원가입 페이지
                    val signUpIntent = Intent(this, AgreeFirstActivity::class.java)
                    signUpIntent.putExtra("token", token)
                    startActivity(signUpIntent)
                } else {
                    // 메인화면
                    val mainIntent = Intent(this, MainActivity::class.java)
                    mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mainIntent)
                }
                finish()
            }
        }
    }
}