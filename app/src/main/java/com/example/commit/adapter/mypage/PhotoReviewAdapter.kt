package com.example.commit.adapter.mypage

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.activity.mypage.PhotoReviewActivity
import com.example.commit.connection.RetrofitClient.UserReview

class PhotoReviewAdapter(
    private val context: Context,
    private val items: List<UserReview>,
    private val reviewerName: String
) : RecyclerView.Adapter<PhotoReviewAdapter.ViewHolder>() {

    override fun getItemCount(): Int = minOf(items.size, 4) // 최대 4장 노출 정책 유지

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_rv, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        // 썸네일 로드
        Glide.with(holder.ivReviewImage)
            .load(item.reviewThumbnail)
            .placeholder(R.drawable.sample_review)
            .error(R.drawable.sample_review)
            .fallback(R.drawable.sample_review)
            .centerCrop()
            .into(holder.ivReviewImage)

        holder.ivReviewImage.setOnClickListener {
            // 클릭 시 상세 화면으로 모든 정보 전달
            val intent = Intent(context, PhotoReviewActivity::class.java).apply {
                putExtra("reviewImageUrl", item.reviewThumbnail)
                putExtra("rate", item.rate)
                putExtra("content", item.content)
                putExtra("createdAt", item.createdAt)
                putExtra("reviewerName", reviewerName)
                putExtra("requestId", item.requestId) // 커미션 타입 텍스트 대체용
            }
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val ivReviewImage: ImageView = v.findViewById(R.id.iv_review_image)
    }
}
