package com.example.commit.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.commit.R
import com.example.commit.databinding.ActivityLoginRoleBinding

/**
 * 로그인 역할 선택 화면을 위한 액티비티입니다.
 * 이 화면은 약관 동의 후 다음 버튼을 클릭했을 때 나타납니다.
 */
class LoginRoleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginRoleBinding

    // 현재 선택된 역할 (null, "applicant", "creator")
    private var selectedRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityLoginRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 next_button 상태 설정: 비활성화
        binding.nextButton.isEnabled = false

        // '신청자' 카드 클릭 리스너 설정
        binding.cardApplicant.setOnClickListener {
            selectRole("applicant")
        }
        // '작가' 카드 클릭 리스너 설정
        binding.cardCreator.setOnClickListener {
            selectRole("creator")
        }

        // '다음' 버튼 클릭 리스너 설정
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                // LoginOnboardingActivity로 화면 전환 Intent 생성
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
            }
        }

        // 뒤로가기 버튼 클릭 리스너
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    /**
     * 역할을 선택하고 UI를 업데이트합니다.
     * @param role 선택된 역할 ("applicant" 또는 "creator")
     */
    private fun selectRole(role: String) {
        selectedRole = role

        // 모든 체크박스와 카드 배경 초기화 (선택 해제)
        binding.checkApplicant.setImageResource(R.drawable.btn_checkbox) // 미체크 이미지
        binding.cardApplicant.setBackgroundResource(R.drawable.btn_gray_rounded) // 기본 배경
        binding.checkCreator.setImageResource(R.drawable.btn_checkbox) // 미체크 이미지
        binding.cardCreator.setBackgroundResource(R.drawable.btn_gray_rounded) // 기본 배경

        // 선택된 역할에 따라 UI 업데이트
        when (role) {
            "applicant" -> {
                binding.checkApplicant.setImageResource(R.drawable.btn_checkbox_checked) // 체크된 이미지
                binding.cardApplicant.setBackgroundResource(R.drawable.btn_mint_rounded) // 민트색 배경
            }
            "creator" -> {
                binding.checkCreator.setImageResource(R.drawable.btn_checkbox_checked) // 체크된 이미지
                binding.cardCreator.setBackgroundResource(R.drawable.btn_mint_rounded) // 민트색 배경
            }
        }
        // 역할 선택 후 '다음' 버튼 활성화 여부 업데이트
        updateNextButtonState()
    }

    /**
     * '다음' 버튼의 활성화 상태를 결정하고 UI를 업데이트합니다.
     * 역할이 하나라도 선택되면 '다음' 버튼을 활성화합니다.
     */
    private fun updateNextButtonState() {
        val shouldEnableButton = selectedRole != null // 역할이 선택되었는지 확인

        binding.nextButton.isEnabled = shouldEnableButton

        // 버튼 활성화 상태에 따라 텍스트 색상 변경 (login_button_gray.xml과 연동)
        // XML의 textColor가 @color/gray2로 고정되어 있다면, 여기서 직접 변경해야 합니다.
        // login_button_gray.xml이 state_enabled를 사용하므로, 텍스트 색상도 selector를 사용하는 것이 좋습니다.
        // 하지만 요청에 따라 코드에서 직접 변경하는 예시를 추가합니다.
        val textColor = if (shouldEnableButton) {
            ContextCompat.getColor(this, R.color.white) // 활성화 시 흰색
        } else {
            ContextCompat.getColor(this, R.color.gray2) // 비활성화 시 gray2
        }
        binding.nextButton.setTextColor(textColor)
    }
}
