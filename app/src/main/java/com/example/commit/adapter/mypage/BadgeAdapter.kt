package com.example.commit.adapter.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R

class BadgeAdapter(
    private val badgeList: List<String>, // URL 목록
    private val onClick: (String) -> Unit // 클릭 시 동작
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBadge: ImageView = itemView.findViewById(R.id.iv_badge_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badgeUrl = badgeList[position]

        // URL 이미지를 Glide로 로드
        Glide.with(holder.itemView.context)
            .load(badgeUrl)
            .placeholder(R.drawable.ic_profile) // 로딩 중 기본 이미지
            .error(R.drawable.ic_profile) // 실패 시 기본 이미지
            .into(holder.ivBadge)

        holder.ivBadge.setOnClickListener {
            onClick(badgeUrl)
        }
    }

    override fun getItemCount(): Int = badgeList.size
}
