package com.example.commit.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.commit.R
import android.Manifest

class ReviewWriteActivity : AppCompatActivity() {

    private var currentRating = 0
    private lateinit var imageSlot: ImageView
    private lateinit var tvCount: TextView
    private lateinit var tvContentError: TextView
    private lateinit var etReview: EditText
    private lateinit var btnSubmit: Button
    private var selectedImageUri: Uri? = null

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_write)

        val btnClose = findViewById<ImageView>(R.id.btnClose)
        btnClose.setOnClickListener { finish() }

        // 별점 뷰 바인딩
        val starViews = listOf(
            findViewById<ImageView>(R.id.star1),
            findViewById(R.id.star2),
            findViewById(R.id.star3),
            findViewById(R.id.star4),
            findViewById(R.id.star5)
        )

        // 별점 UI 갱신 함수
        fun updateStars(rating: Int) {
            currentRating = rating
            starViews.forEachIndexed { index, imageView ->
                imageView.setImageResource(
                    if (index < rating) R.drawable.ic_star_on else R.drawable.ic_star_off
                )
            }

            val length = etReview.text?.length ?: 0
            btnSubmit.isEnabled = length >= 10 && currentRating > 0
        }

        // 별점 클릭 이벤트 설정
        starViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                updateStars(index + 1)
            }
        }

        // 뷰 바인딩
        etReview = findViewById(R.id.etReview)
        btnSubmit = findViewById(R.id.next_button)
        tvCount = findViewById(R.id.tvCount)
        tvContentError = findViewById(R.id.tvContentError)
        imageSlot = findViewById(R.id.ivCameraIcon)

        val requestId = intent.getIntExtra("requestId", -1)

        // 글자 수 실시간 반영 + 유효성 검사
        etReview.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                tvCount.text = "$length/1000"

                btnSubmit.isEnabled = length >= 10 && currentRating > 0
                tvContentError.visibility = if (length in 1..9) TextView.VISIBLE else TextView.GONE
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // 이미지 선택
        imageSlot.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        // 작성 완료 버튼
        btnSubmit.setOnClickListener {
            val reviewText = etReview.text.toString().trim()

            if (currentRating == 0) {
                Toast.makeText(this, "별점을 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (reviewText.length < 10) {
                Toast.makeText(this, "리뷰를 10자 이상 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: 서버 전송
            Toast.makeText(this, "리뷰가 등록되었습니다. (ID: $requestId)", Toast.LENGTH_SHORT).show()
            finish()
        }

        // 권한 요청
        requestStoragePermission()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                imageSlot.setImageURI(imageUri)
                selectedImageUri = imageUri
            }
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1001)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001)
            }
        }
    }
}
