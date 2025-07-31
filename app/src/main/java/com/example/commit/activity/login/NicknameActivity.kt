package com.example.commit.activity.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.commit.R
import com.example.commit.activity.MainActivity
import com.example.commit.databinding.ActivityLoginNicknameBinding

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

        // '다음' 버튼 클릭 리스너 설정
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                //val nickname = binding.inputNickname.text.toString()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("start_fragment", R.id.nav_home)
                intent.putExtra("show_signup_bottom_sheet", true)
                startActivity(intent)
                finish()
            }
        }

        // 뒤로가기 버튼 클릭 리스너 (XML에 back_button ID가 있다고 가정)
        binding.backButton.setOnClickListener {
            finish() // 이전 화면으로 돌아가기
        }
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
}
