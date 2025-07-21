package com.example.commit.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.R
import com.example.commit.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 버튼 상태 비활성화
        setApplyButtonState(false)

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener { finish() }

        // 닉네임/소개 입력 감시
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateApplyButtonState()
                // 닉네임 유효성 안내문구 색상 처리
                val nickname = binding.etNickname.text.toString()
                val isValid = nickname.matches(Regex("^[가-힣a-zA-Z0-9]*$"))
                val isLengthValid = nickname.length <= 10
                if (!isValid || !isLengthValid) {
                    binding.tvNicknameGuide.setTextColor(Color.parseColor("#FF3B30"))
                } else {
                    binding.tvNicknameGuide.setTextColor(getColorResource(R.color.gray2))
                }
                binding.etNickname.setTextColor(getColorResource(R.color.black2))
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        binding.etNickname.addTextChangedListener(watcher)

        binding.etNickname.filters = arrayOf(InputFilter.LengthFilter(10))
        // 소개 글 감시
        binding.etIntro.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateApplyButtonState()
                val length = s?.length ?: 0
                binding.tvIntroCount.text = "$length"
                binding.etIntro.setTextColor(getColorResource(R.color.black2))
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 적용하기 버튼 클릭
        binding.btnApply.setOnClickListener {
            if (binding.btnApply.isEnabled) {
                Toast.makeText(this, "프로필이 수정되었습니다.", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    finish()
                }, 2000)
            }
        }
    }

    private fun updateApplyButtonState() {
        val nickname = binding.etNickname.text.toString().trim()
        val intro = binding.etIntro.text.toString().trim()
        val enabled = nickname.isNotEmpty() && intro.isNotEmpty()
        setApplyButtonState(enabled)
    }

    private fun setApplyButtonState(enabled: Boolean) {
        binding.btnApply.isEnabled = enabled
        if (enabled) {
            binding.btnApply.setBackgroundResource(R.drawable.btn_edit2)
        } else {
            binding.btnApply.setBackgroundResource(R.drawable.btn_edit1)
        }
    }

    private fun getColorResource(colorResId: Int): Int {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            resources.getColor(colorResId, null)
        } else {
            @Suppress("DEPRECATION")
            resources.getColor(colorResId)
        }
    }
} 