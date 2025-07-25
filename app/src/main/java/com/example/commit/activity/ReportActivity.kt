package com.example.commit.activity

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.commit.R
import com.example.commit.databinding.ActivityCommissionReportBinding

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommissionReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommissionReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼
        binding.ivBack.setOnClickListener {
            finish()
        }

        // 저장하기 버튼
        binding.btnSave.setOnClickListener {
            saveReportImage()
        }

        // 필요시 데이터 바인딩 예시
        val nickname = "로지" // <- 나중에 서버에서 받아올 값
        val monthText = "6월" // <- 강조할 대상
        val color = ContextCompat.getColor(this, R.color.mint2)

        val builder = SpannableStringBuilder()
            .append("${nickname}님의 ".toString())
            .append(SpannableString(monthText).apply {
                setSpan(
                    ForegroundColorSpan(color),
                    0,
                    length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            })
            .append(" 리포트예요!")

        binding.tvSubtitle.text = builder

        binding.tvCategory.text = "그림 (10건)"
        binding.tvAuthor.text = "키르"
        binding.tvPoint.text = "177,000P"
        binding.tvReview.text = "77.7%"
    }

    private fun saveReportImage() {
        // 캡처 대상: bg_mint_area ~ tv_save_tip 바로 위까지
        binding.root.post {
            val topY = binding.bgMintArea.top
            val bottomY = binding.tvSaveTip.top
            val bitmap = getBitmapFromPartialView(binding.root, topY, bottomY)

            if (bitmap != null) {
                saveBitmapToGallery(bitmap)
                Toast.makeText(this, "리포트 이미지가 저장되었어요!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "이미지 저장에 실패했어요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun getBitmapFromPartialView(view: android.view.View, startY: Int, endY: Int): Bitmap? {
        if (view.width == 0 || view.height == 0 || endY <= startY) return null

        // 전체 View를 Bitmap으로
        val fullBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(fullBitmap)
        view.draw(canvas)

        // 원하는 영역만 잘라냄
        return Bitmap.createBitmap(fullBitmap, 0, startY, view.width, endY - startY)
    }

    private fun saveBitmapToGallery(bitmap: Bitmap) {
        val filename = "report_${System.currentTimeMillis()}.png"
        val mimeType = "image/png"

        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, mimeType)
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CommissionReports")
            put(android.provider.MediaStore.Images.Media.IS_PENDING, 1)
        }

        val contentResolver = contentResolver
        val uri = contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            contentResolver.openOutputStream(it)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            contentValues.clear()
            contentValues.put(android.provider.MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(uri, contentValues, null, null)
        }
    }

}
