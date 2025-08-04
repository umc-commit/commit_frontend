package com.example.commit.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.ActivityLoginNicknameBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NicknameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginNicknameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityLoginNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 next_button 상태 설정: 비활성화 및 투명도 조절
        binding.nextButton.isEnabled = false
        // 초기 next_button 텍스트 색상 설정
        binding.nextButton.setTextColor(ContextCompat.getColor(this, R.color.gray2))

        // 닉네임 입력 필드에 TextWatcher 추가
        binding.inputNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중
                updateNextButtonState(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후
            }
        })

        // 다음 버튼 클릭 → 회원가입 API 호출
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                val nickname = binding.inputNickname.text.toString()

                // 이전 Activity에서 전달받은 값들
                val token = intent.getStringExtra("token") ?: ""
                val role = intent.getStringExtra("role") ?: "client"
                val categories = intent.getIntegerArrayListExtra("categories") ?: arrayListOf()
                val agreements = intent.getIntegerArrayListExtra("agreements") ?: arrayListOf()

                // API 요청 객체 생성
                val request = RetrofitClient.RequestSignUp(
                    token = token,
                    nickname = nickname,
                    role = role,
                    categories = categories,
                    agreements = agreements
                )

                signUp(request)
            }
        }

        // 뒤로가기 버튼
        binding.backButton.setOnClickListener { finish() }
    }

    /**
     * 닉네임 유효성을 검사하고 '다음' 버튼의 활성화 상태를 업데이트합니다.
     * 닉네임은 띄어쓰기 없이 한글, 영문, 숫자 10자 이하만 가능합니다.
     */
    private fun updateNextButtonState(nickname: String) {
        // 닉네임 유효성 검사 (띄어쓰기 없이 한글, 영문, 숫자 10자 이하)
        val isValid = nickname.matches("^[가-힣a-zA-Z0-9]{1,10}$".toRegex())

        // 경고 텍스트 가시성 설정
        if (nickname.isEmpty()) {
            // 닉네임이 비어있으면 경고 텍스트 숨김 (초기 상태)
            binding.warningText.visibility = View.GONE
        } else if (isValid) {
            // 유효한 닉네임이면 경고 텍스트 숨김
            binding.warningText.visibility = View.GONE
        } else {
            // 유효하지 않은 닉네임이면 경고 텍스트 표시
            binding.warningText.visibility = View.VISIBLE
        }

        // '다음' 버튼 활성화 상태 설정 (닉네임이 비어있지 않고 유효할 때만 활성화)
        val shouldEnableButton = nickname.isNotEmpty() && isValid
        binding.nextButton.isEnabled = shouldEnableButton

        // 버튼 활성화 상태에 따라 텍스트 색상 변경
        val textColor = if (shouldEnableButton) {
            ContextCompat.getColor(this, R.color.white) // 활성화 시 흰색
        } else {
            ContextCompat.getColor(this, R.color.gray2) // 비활성화 시 gray2
        }
        binding.nextButton.setTextColor(textColor)
    }

    private fun signUp(request: RetrofitClient.RequestSignUp) {
        val api = RetrofitObject.getRetrofitService(this)

        Log.d("SignUpAPI", "Request Data: $request")
        api.signUp(request).enqueue(object : Callback<RetrofitClient.ResponseSignUp> {
            override fun onResponse(
                call: Call<RetrofitClient.ResponseSignUp>,
                response: Response<RetrofitClient.ResponseSignUp>
            ) {
                Log.d("SignUpAPI", "HTTP Code: ${response.code()}")
                Log.d("SignUpAPI", "Error Body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.resultType == "SUCCESS") {
                        // 회원가입 성공 시 닉네임 저장
                        val prefs = getSharedPreferences("auth", MODE_PRIVATE)
                        prefs.edit()
                            .putString("nickname", body.success?.user?.nickname ?: "")
                            .apply()

                        // 메인 화면 이동
                        val intent = Intent(this@NicknameActivity, MainActivity::class.java)
                        intent.putExtra("start_fragment", R.id.nav_home)
                        intent.putExtra("show_signup_bottom_sheet", true)
                        startActivity(intent)
                        finish()
                    } else {
                        // 회원가입 실패 시 서버 사유 표시
                        Toast.makeText(
                            this@NicknameActivity,
                            body?.error?.reason ?: "회원가입 실패",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(this@NicknameActivity, "서버 오류", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RetrofitClient.ResponseSignUp>, t: Throwable) {
                Toast.makeText(this@NicknameActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
