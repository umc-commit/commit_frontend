package com.example.commit.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

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

        // 적용 버튼 클릭 시 결과 전달
        binding.btnApply.setOnClickListener {
            if (binding.btnApply.isEnabled) {
                val nickname = binding.etNickname.text.toString()
                val imageUri = selectedImageUri?.toString()

                val resultIntent = Intent().apply {
                    putExtra("nickname", nickname)
                    putExtra("imageUri", imageUri)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

        // 갤러리에서 이미지 선택하고 결과를 받아 ivProfile에 적용
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImageUri = result.data?.data
                if (selectedImageUri != null) {
                    // 이미지 URI에 대한 접근 권한을 영구적으로 확보
                    contentResolver.takePersistableUriPermission(
                        selectedImageUri!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    Glide.with(this)
                        .load(selectedImageUri)
                        .into(binding.ivProfile)
                } else {
                    Toast.makeText(this, "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 갤러리 앱을 열어 이미지 선택 요청을 보냄
        val openGallery = {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            imagePickerLauncher.launch(intent)
        }

        // 프로필 이미지 또는 카메라 아이콘 클릭 시 갤러리 열기
        binding.ivProfile.setOnClickListener { openGallery() }
        binding.ivCamera.setOnClickListener { openGallery() }

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