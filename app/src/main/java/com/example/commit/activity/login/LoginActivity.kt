package com.example.commit.activity.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.googleLogin.setOnClickListener {
            val intent = Intent(this, AgreeFirstActivity::class.java)
            startActivity(intent)
        }

    }
}