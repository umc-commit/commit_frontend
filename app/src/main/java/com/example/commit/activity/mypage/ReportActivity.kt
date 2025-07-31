package com.example.commit.activity.mypage

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
import com.example.commit.data.model.entities.ReportProfile

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommissionReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommissionReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 테스트용 리포트 타입 (API 연결 전 수동 지정)
        val reportType = ReportType.GEM_HUNTER

        // UI 적용
        applyReportTypeUI(reportType)

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
        // tv_description만 잠깐 숨겼다가 다시 보이게
        binding.tvDescription.visibility = android.view.View.INVISIBLE

        // 캡처 대상: bg_mint_area ~ tv_save_tip 바로 위까지
        binding.root.post {
            val topY = binding.bgMintArea.top
            val bottomY = binding.tvSaveTip.top
            val bitmap = getBitmapFromPartialView(binding.root, topY, bottomY)

            binding.tvDescription.visibility = android.view.View.VISIBLE

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

    enum class ReportType {
        VIP,             // 커미션계의 VIP
        FAN_APPLY,       // 작가 덕후 신청자
        CURIOUS_APPLY,   // 호기심 대장 신청자
        GEM_HUNTER,      // 숨겨진 보석 발굴가
        FAST_FEEDBACK    // 빠른 피드백러
    }

    private val reportProfiles = mapOf(
        ReportType.VIP to ReportProfile(
            imageResId = R.drawable.report_cat_1,
            badgeText = "커미션계의 VIP",
            quoteText = "“커미션계의 큰 손 등장!”\n덕분에 작가님들의 창작활동이 풍요로워졌어요."
        ),
        ReportType.FAN_APPLY to ReportProfile(
            imageResId = R.drawable.report_cat_2,
            badgeText = "작가 덕후 신청자",
            quoteText = "“이 작가님만큼은 믿고 맡긴다!”\n단골의 미덕을 지닌 당신, 작가님도 감동했을 거예요."
        ),
        ReportType.CURIOUS_APPLY to ReportProfile(
            imageResId = R.drawable.report_cat_3,
            badgeText = "호기심 대장 신청자",
            quoteText = "“다양한 스타일을 사랑하는 탐험가!”\n호기심이 가득해서, 언제나 새로운 작가를 탐색해요."
        ),
        ReportType.GEM_HUNTER to ReportProfile(
            imageResId = R.drawable.report_cat_4,
            badgeText = "숨겨진 보석 발굴가",
            quoteText = "“빛나는 원석을 내가 발견했다!”\n성장하는 작가님들의 첫걸음을 함께한 당신, 멋져요."
        ),
        ReportType.FAST_FEEDBACK to ReportProfile(
            imageResId = R.drawable.report_cat_5,
            badgeText = "빠른 피드백러",
            quoteText = "“작가님, 이번 커미션 최고였어요!”\n정성 가득한 피드백으로 건강한 커미션 문화를 만들어가요."
        )
    )

    private fun applyReportTypeUI(reportType: ReportType) {
        val profile = reportProfiles[reportType] ?: return

        binding.ivCharacter.setImageResource(profile.imageResId)
        binding.tvBadge.text = profile.badgeText
        binding.tvQuote.text = profile.quoteText
    }

}
