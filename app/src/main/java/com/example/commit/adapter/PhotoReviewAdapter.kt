package com.example.commit.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.activity.PhotoReviewActivity

class PhotoReviewAdapter(
    private val context: Context,
    private val itemList: List<Int> // 예시로 이미지 리소스 id 리스트
) : RecyclerView.Adapter<PhotoReviewAdapter.ViewHolder>() {

    override fun getItemCount(): Int = minOf(itemList.size, 4)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo_rv, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resId = itemList[position]
        holder.ivReviewImage.setImageResource(resId)
        holder.ivReviewImage.setOnClickListener {
            val intent = Intent(context, PhotoReviewActivity::class.java)
            context.startActivity(intent)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivReviewImage: ImageView = view.findViewById(R.id.iv_review_image)
    }

} 