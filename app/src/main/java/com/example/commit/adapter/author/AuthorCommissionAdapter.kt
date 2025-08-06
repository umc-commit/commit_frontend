package com.example.commit.adapter.author

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient

class AuthorCommissionAdapter(
    private val items: MutableList<RetrofitClient.AuthorCommissionItem>
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
        // API에는 커미션 이미지 필드가 없으므로 placeholder 사용
        Glide.with(holder.itemView.context)
            .load(R.drawable.image_placeholder) // 실제 이미지 필드가 추가되면 수정
            .placeholder(R.drawable.image_placeholder)
            .into(holder.thumbnail)

        // 텍스트 설정
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.summary
        holder.tvTagDrawing.text = item.category
        holder.tvTagLD.text = item.tags.getOrNull(0) ?: ""
        holder.tvTagCouple.text = item.tags.getOrNull(1) ?: ""

        /*// 북마크 상태
        holder.ivBookmark.setImageResource(
            if (item.isBookmarked) R.drawable.ic_home_bookmark_on
            else R.drawable.ic_home_bookmark
        )

        // 북마크 클릭 토글
        holder.ivBookmark.setOnClickListener {
            item.isBookmarked = !item.isBookmarked
            notifyItemChanged(position)
        }*/
        holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
        // 북마크 클릭 이벤트 (서버 연동 없이 UI만 토글)
        holder.ivBookmark.setOnClickListener {
            // 북마크 토글 로직 필요 시 추가
        }
    }

    override fun getItemCount(): Int = minOf(items.size, 6)
}
