package com.example.commit.adapter.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R

class ImageListAdapter(
    private val imageUrls: List<String>,
    private val onImageClick: (() -> Unit)? = null
) : RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following_thumbnail, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageUrls[position])
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .fallback(R.drawable.image_placeholder)
            .centerCrop()
            .into(holder.imageView)

        // 이미지 클릭 → 상세 이동
        holder.itemView.setOnClickListener { onImageClick?.invoke() }
    }

    override fun getItemCount(): Int = imageUrls.size
    override fun onViewRecycled(holder: ImageViewHolder) {
        super.onViewRecycled(holder)
        Glide.with(holder.itemView.context).clear(holder.imageView)
    }
}
