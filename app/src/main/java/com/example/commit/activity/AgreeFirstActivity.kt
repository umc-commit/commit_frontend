package com.example.commit.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.commit.R
import com.example.commit.databinding.ActivityAgreefirstBinding

/**
 * 초기 약관 동의 화면을 위한 액티비티입니다.
 * 체크박스 상태에 따라 '다음' 버튼을 활성화하고,
 * 특정 조건 충족 시 다음 화면으로 전환합니다.
 */
class AgreeFirstActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgreefirstBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityAgreefirstBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 next_button 상태 설정: 비활성화 및 투명도 조절
        binding.nextButton.isEnabled = false

        // 각 체크박스(ImageView)에 클릭 리스너 설정
        binding.checkIv0.setOnClickListener { toggleAllCheckboxes() } // '모두 동의' 체크박스
        binding.checkIv1.setOnClickListener { toggleCheckbox(binding.checkIv1) } // 필수 약관 1
        binding.checkIv2.setOnClickListener { toggleCheckbox(binding.checkIv2) } // 필수 약관 2
        binding.checkIv3.setOnClickListener { toggleCheckbox(binding.checkIv3) } // 필수 약관 3

        // '다음' 버튼 클릭 리스너 설정
        binding.nextButton.setOnClickListener {
            // 버튼이 활성화된 상태에서만 클릭 동작 수행
            if (binding.nextButton.isEnabled) {
                // LoginRoleActivity로 화면 전환 Intent 생성
                val intent = Intent(this, LoginRoleActivity::class.java)
                startActivity(intent)
            }
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // 액티비티 로드 시 초기 버튼 상태 업데이트
        updateNextButtonState()
    }

    /**
     * 개별 체크박스의 선택 상태를 토글하고 UI를 업데이트합니다.
     * @param imageView 상태를 변경할 ImageView (체크박스 역할)
     */
    private fun toggleCheckbox(imageView: ImageView) {
        // 현재 선택 상태를 반전시킴
        imageView.isSelected = !imageView.isSelected


        // 개별 체크박스가 해제되면 '모두 동의' 체크박스도 해제
        if (!imageView.isSelected && imageView != binding.checkIv0) {
            binding.checkIv0.isSelected = false
        }

        // 체크박스 상태 변경 후 '다음' 버튼 활성화 여부 업데이트
        updateNextButtonState()
    }

    /**
     * '모두 동의' 체크박스 클릭 시 모든 체크박스의 상태를 일괄적으로 토글하고 UI를 업데이트합니다.
     */
    private fun toggleAllCheckboxes() {
        // '모두 동의' 체크박스의 새 상태 (현재 상태의 반전)
        val newState = !binding.checkIv0.isSelected

        // 모든 체크박스의 상태를 새 상태로 설정
        binding.checkIv0.isSelected = newState
        binding.checkIv1.isSelected = newState
        binding.checkIv2.isSelected = newState
        binding.checkIv3.isSelected = newState


        // 모든 체크박스 상태 변경 후 '다음' 버튼 활성화 여부 업데이트
        updateNextButtonState()
    }

    /**
     * '다음' 버튼의 활성화 상태를 결정하고 UI를 업데이트합니다.
     * 활성화 조건:
     * 1. check_iv1 AND check_iv2가 모두 체크
     * 2. check_iv1 AND check_iv2 AND check_iv3가 모두 체크
     * 3. check_iv0이 체크 (이 경우 모든 필수 약관이 체크된 것으로 간주)
     */
    private fun updateNextButtonState() {
        val isIv1Checked = binding.checkIv1.isSelected
        val isIv2Checked = binding.checkIv2.isSelected
        val isIv3Checked = binding.checkIv3.isSelected

        // check_iv1, check_iv2, check_iv3가 모두 체크되었는지 확인
        val allRequiredAndOptionalChecked = isIv1Checked && isIv2Checked && isIv3Checked

        // 다른 체크박스들의 상태에 따라 check_iv0의 isSelected 상태 업데이트
        if (allRequiredAndOptionalChecked && !binding.checkIv0.isSelected) {
            // 모든 필수 및 선택 약관이 체크되었고, '모두 동의'가 체크되지 않았다면 '모두 동의'를 체크
            binding.checkIv0.isSelected = true
        } else if (!allRequiredAndOptionalChecked && binding.checkIv0.isSelected) {
            // 모든 필수 및 선택 약관이 체크되지 않았는데, '모두 동의'가 체크되어 있다면 '모두 동의'를 해제
            binding.checkIv0.isSelected = false
        }
        val shouldEnableButton = (isIv1Checked && isIv2Checked) || binding.checkIv0.isSelected


        // '다음' 버튼의 활성화 상태 설정
        binding.nextButton.isEnabled = shouldEnableButton
        val textColor = if (shouldEnableButton) {
            ContextCompat.getColor(this, R.color.white) // 활성화 시 흰색
        } else {
            ContextCompat.getColor(this, R.color.gray2) // 비활성화 시 gray2
        }
        binding.nextButton.setTextColor(textColor)
    }
}
