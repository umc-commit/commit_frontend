package com.example.commit.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.activity.login.LoginActivity
import com.example.commit.databinding.SplashBinding

class SplashActivity: AppCompatActivity() {
    lateinit var binding: SplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thread를 사용하여 1초 대기 후에 로그인 상태에 따라 이동
        val thread = object : Thread() {
            override fun run() {
                try {
                    sleep(1000) // 1초 대기
                    
                    // 토큰 확인
                    val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                    val token = prefs.getString("accessToken", null)
                    
                    val intent = if (token.isNullOrEmpty()) {
                        // 토큰이 없으면 로그인 화면으로
                        Intent(this@SplashActivity, LoginActivity::class.java)
                    } else {
                        // 토큰이 있으면 메인 화면으로
                        Intent(this@SplashActivity, MainActivity::class.java)
                    }
                    
                    startActivity(intent)
                    finish() // 현재 액티비티를 종료
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        thread.start()
    }
}