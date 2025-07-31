package com.example.commit.adapter.author

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.data.model.entities.AuthorCommission

class AuthorCommissionAdapter(
    private val items: MutableList<AuthorCommission>
) : RecyclerView.Adapter<AuthorCommissionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDescription: TextView = view.findViewById(R.id.tv_description)
        val tvTagDrawing: TextView = view.findViewById(R.id.tv_tag_drawing)
        val tvTagLD: TextView = view.findViewById(R.id.tv_tag_ld)
        val tvTagCouple: TextView = view.findViewById(R.id.tv_tag_couple)
        val ivBookmark: ImageView = view.findViewById(R.id.iv_bookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author_pf_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 썸네일 이미지
        Glide.with(holder.itemView)
            .load(item.thumbnailResId)
            .placeholder(R.drawable.image_placeholder)
            .into(holder.thumbnail)

        // 텍스트 설정
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
        holder.tvTagDrawing.text = item.tagDrawing
        holder.tvTagLD.text = item.tagLD
        holder.tvTagCouple.text = item.tagCouple

        // 북마크 상태
        holder.ivBookmark.setImageResource(
            if (item.isBookmarked) R.drawable.ic_home_bookmark_on
            else R.drawable.ic_home_bookmark
        )

        // 북마크 클릭 토글
        holder.ivBookmark.setOnClickListener {
            item.isBookmarked = !item.isBookmarked
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = minOf(items.size, 4)
}
