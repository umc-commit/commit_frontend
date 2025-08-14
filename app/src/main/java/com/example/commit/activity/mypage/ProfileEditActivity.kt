package com.example.commit.activity.mypage

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitAPI
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.ActivityProfileEditBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ProfileActivity에서 넘어온 기존 데이터 세팅
        val currentNickname = intent.getStringExtra("nickname")
        val currentIntro = intent.getStringExtra("intro")

        if (!currentNickname.isNullOrEmpty()) {
            binding.etNickname.setText(currentNickname)
        }
        if (!currentIntro.isNullOrEmpty() && currentIntro != "입력된 소개가 없습니다.") {
            binding.etIntro.setText(currentIntro)
            binding.tvIntroCount.text = currentIntro.length.toString()
        }

        // 프로필 이미지 URL 세팅 (있으면 미리 로드)
        val currentProfileImageUrl = intent.getStringExtra("profileImage")
        if (!currentProfileImageUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(currentProfileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.ivProfile)
        }

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

        // 적용 버튼 클릭 시 결과 전달, 닉네임 변경 여부
        binding.btnApply.setOnClickListener {
            if (!binding.btnApply.isEnabled) return@setOnClickListener

            val nickname = binding.etNickname.text.toString().trim()
            val intro = binding.etIntro.text.toString().trim()
            val imageUri = selectedImageUri?.toString()

            val api = RetrofitObject.getRetrofitService(this)

            val originalNickname = intent.getStringExtra("nickname") ?: ""
            val originalIntro = intent.getStringExtra("intro") ?: ""

            // 변경된 내용이 없으면 종료
            if (nickname == originalNickname && intro == originalIntro) {
                Toast.makeText(this@ProfileEditActivity, "변경된 내용이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 닉네임이 안 바뀌면 바로 PATCH
            if (nickname == originalNickname) {
                updateProfile(api, nickname, intro, imageUri, originalNickname, originalIntro)
            } else {
                api.checkNickname(nickname).enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.NicknameCheckResponse>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.NicknameCheckResponse>>,
                        response: Response<RetrofitClient.ApiResponse<RetrofitClient.NicknameCheckResponse>>
                    ) {
                        val body = response.body()
                        val msg = body?.success?.message

                        // 서버 스펙: 200 + "중복된 닉네임입니다." 도 존재
                        if (msg == "중복된 닉네임입니다.") {
                            Toast.makeText(this@ProfileEditActivity, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show()
                            return
                        }

                        // 사용 가능(예: "사용 가능한 닉네임입니다.")이면 PATCH 진행
                        if (response.isSuccessful) {
                            updateProfile(api, nickname, intro, imageUri, originalNickname, originalIntro)
                            return
                        }

                        // 그 외 에러
                        Log.d("ProfileEdit", "닉네임 확인 실패(${response.code()})")
                    }

                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.NicknameCheckResponse>>,
                        t: Throwable
                    ) {
                        Log.d("ProfileEdit", "네트워크 오류: ${t.message}")
                    }
                })
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

    // PATCH 호출부 — 변경된 필드만 담아서 보냄 + result 타입 네임스페이스 수정
    private fun updateProfile(
        api: RetrofitAPI,
        nickname: String,
        intro: String,
        imageUri: String?,
        originalNickname: String,
        originalIntro: String
    ) {
        val request = RetrofitClient.ProfileUpdateRequest(
            nickname = if (nickname != originalNickname) nickname else null,
            description = if (intro != originalIntro) intro else null,
            profileImage = if (imageUri != null) imageUri else null
        )

        api.updateMyProfile(request).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.ProfileUpdateResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileUpdateResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.ProfileUpdateResponse>>
            ) {
                if (response.isSuccessful && response.body()?.resultType == "SUCCESS") {
                    val resultIntent = Intent().apply {
                        putExtra("nickname", nickname)
                        putExtra("imageUri", imageUri)
                        putExtra("intro", intro)
                    }
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this@ProfileEditActivity, "프로필 수정 실패(${response.code()})", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ProfileUpdateResponse>>,
                t: Throwable
            ) {
                Log.d("ProfileEdit", "네트워크 오류: ${t.message}")
            }
        })
    }
} 