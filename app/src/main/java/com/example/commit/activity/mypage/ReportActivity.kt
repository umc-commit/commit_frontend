package com.example.commit.activity.mypage

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.ActivityCommissionReportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

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

        fetchReportData()
    }


    private fun fetchReportData() {
        val service = RetrofitObject.getRetrofitService(this)
        service.getCommissionReport().enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>>
            ) {
                if (response.isSuccessful) {
                    val report = response.body()?.success
                    if (report != null) {
                        bindReportUI(report)
                    } else {
                        Log.e("ReportAPI", "success 데이터가 없음")
                    }
                } else {
                    Log.e("ReportAPI", "API 실패: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.ReportResponseData>>,
                t: Throwable
            ) {
                Log.e("ReportAPI", "네트워크 오류", t)
            }
        })
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

    private fun bindReportUI(report: RetrofitClient.ReportResponseData) {
        val monthText = "${report.reportInfo.month}월"
        val color = ContextCompat.getColor(this, R.color.mint2)
        val builder = SpannableStringBuilder()
            .append("${report.reportInfo.userNickname}님의 ")
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

        Glide.with(this).load(report.characterImage).placeholder(R.drawable.ic_profile) // 로딩 중 표시
            .error(R.drawable.ic_profile).into(binding.ivCharacter)
        binding.tvBadge.text = report.quote.title
        binding.tvQuote.text = report.quote.description
        binding.tvCategory.text = "${report.statistics.mainCategory.name} (${report.statistics.mainCategory.count}건)"
        Glide.with(this).load(report.statistics.favoriteArtist.profileImage).placeholder(R.drawable.ic_profile)
            .error(R.drawable.ic_profile).into(binding.ivProfile)
        binding.tvAuthor.text = report.statistics.favoriteArtist.nickname
        binding.tvPoint.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(report.statistics.pointsUsed)}P"
        binding.tvReview.text = "${(report.statistics.reviewRate * 100).toInt()}%"
    }


}
