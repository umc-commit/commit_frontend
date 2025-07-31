package com.example.commit.activity.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.commit.R
import com.example.commit.databinding.ActivityOnboardingBinding

/**
 * 온보딩 화면을 위한 액티비티입니다.
 * 역할 선택 후 다음 버튼을 클릭했을 때 나타납니다.
 * 관심 있는 주제를 최대 3개까지 선택할 수 있으며, 선택 상태에 따라 UI가 변경되고 '다음' 버튼이 활성화됩니다.
 */
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val selectedTopics = mutableListOf<String>() // 선택된 주제의 이름을 저장할 리스트

    // XML에 정의된 각 주제 아이템 LinearLayout들을 리스트로 관리 (편의를 위해)
    private val topicItemViews by lazy {
        listOf(
            binding.itemText,
            binding.itemDrawing,
            binding.itemVideo,
            binding.itemDesign,
            binding.itemGoods,
            binding.itemFortune,
            binding.itemSound,
            binding.itemMotion,
            binding.itemOutsource
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 next_button 상태 설정: 비활성화 및 투명도 조절
        binding.nextButton.isEnabled = false
        // 초기 next_button 텍스트 색상 설정
        binding.nextButton.setTextColor(ContextCompat.getColor(this, R.color.gray2))

        // 각 주제 아이템에 클릭 리스너 설정
        topicItemViews.forEach { itemLayout ->
            itemLayout.setOnClickListener {
                // tag 속성에서 주제 이름 가져오기 (예: "글", "그림")
                val topicName = itemLayout.tag.toString()
                toggleTopicSelection(itemLayout, topicName)
            }
        }

        // '다음' 버튼 클릭 리스너 설정
        binding.nextButton.setOnClickListener {
            if (binding.nextButton.isEnabled) {
                val intent = Intent(this, NicknameActivity::class.java)
                startActivity(intent)
            }
        }

        // 뒤로가기 버튼 클릭
        binding.backButton.setOnClickListener {
            finish() // 이전 화면으로 돌아가기
        }

        // 액티비티 로드 시 초기 버튼 상태 업데이트
        updateNextButtonState()
    }

    /**
     * 주제 아이템의 선택 상태를 토글하고 UI를 업데이트합니다.
     * @param itemLayout 선택/해제할 주제 아이템의 LinearLayout
     * @param topicName 해당 주제의 이름 (LinearLayout의 tag에서 가져옴)
     */
    private fun toggleTopicSelection(itemLayout: LinearLayout, topicName: String) {
        val isSelected = itemLayout.isSelected // 현재 선택 상태 확인

        // 해당 아이템 내의 TextView (라벨) 찾기
        val labelTextView: TextView? = when (itemLayout.id) {
            R.id.item_text -> binding.labelText
            R.id.item_drawing -> binding.labelDrawing
            R.id.item_video -> binding.labelVideo
            R.id.item_design -> binding.labelDesign
            R.id.item_goods -> binding.labelGoods
            R.id.item_fortune -> binding.labelFortune
            R.id.item_sound -> binding.labelSound
            R.id.item_motion -> binding.labelMotion
            R.id.item_outsource -> binding.labelOutsource
            else -> null
        }

        if (isSelected) {
            // 이미 선택된 상태이므로, 선택 해제
            itemLayout.isSelected = false
            selectedTopics.remove(topicName)
            itemLayout.setBackgroundResource(R.drawable.btn_rounded) // 기본 배경으로 변경
            labelTextView?.setTextColor(ContextCompat.getColor(this, R.color.black1)) // 기본 텍스트 색상으로 변경
            binding.warningText.visibility = View.GONE // 경고 텍스트 숨김
        } else {
            // 선택되지 않은 상태이므로, 선택 시도
            if (selectedTopics.size < 3) {
                // 최대 선택 개수(3개) 미만이므로 선택 허용
                itemLayout.isSelected = true
                selectedTopics.add(topicName)
                itemLayout.setBackgroundResource(R.drawable.btn_mint_rounded) // 선택된 배경으로 변경
                labelTextView?.setTextColor(ContextCompat.getColor(this, R.color.mint2)) // 선택된 텍스트 색상으로 변경
                binding.warningText.visibility = View.GONE // 경고 텍스트 숨김
            } else {
                // 최대 선택 개수 초과
                binding.warningText.visibility = View.VISIBLE // 경고 텍스트 표시
            }
        }
        // 선택 상태 변경 후 '다음' 버튼 활성화 여부 업데이트
        updateNextButtonState()
    }

    /**
     * '다음' 버튼의 활성화 상태를 결정하고 UI를 업데이트합니다.
     * 선택된 주제가 1개 이상일 때 버튼을 활성화합니다.
     */
    private fun updateNextButtonState() {
        val shouldEnableButton = selectedTopics.isNotEmpty() // 선택된 주제가 하나라도 있는지 확인

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
