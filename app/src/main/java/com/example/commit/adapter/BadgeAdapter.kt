package com.example.commit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R

class BadgeAdapter(
    private val badgeList: List<Int>, // drawable id 목록
    private val onClick: (Int) -> Unit // 클릭 시 동작
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    inner class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBadge: ImageView = itemView.findViewById(R.id.iv_badge_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badgeResId = badgeList[position]
        holder.ivBadge.setImageResource(badgeResId)

        holder.ivBadge.setOnClickListener {
            onClick(badgeResId)
        }
    }

    override fun getItemCount(): Int = badgeList.size
}
