package com.example.commit.adapter.author

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.data.model.entities.AuthorReview

class AuthorReviewAdapter(
    private val items: List<AuthorReview>
) : RecyclerView.Adapter<AuthorReviewAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRating: TextView = view.findViewById(R.id.tv_rating)
        val tvCommissionName: TextView = view.findViewById(R.id.tv_commission_name)
        val tvReviewContent: TextView = view.findViewById(R.id.tv_review_content)
        val tvReviewer: TextView = view.findViewById(R.id.tv_reviewer)
        val tvTime: TextView = view.findViewById(R.id.tv_time)
        val tvWorkPeriod: TextView = view.findViewById(R.id.tv_work_period)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author_pf_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // 별점
        holder.tvRating.text = String.format("%.1f", item.rating)

        // 커미션명
        holder.tvCommissionName.text = item.commissionName

        // 리뷰 내용
        holder.tvReviewContent.text = item.content

        // 작성자
        holder.tvReviewer.text = item.reviewer

        // 작성 시간
        holder.tvTime.text = item.time

        // 작업기간
        holder.tvWorkPeriod.text = "작업기간 : ${item.workPeriod}"
    }

    override fun getItemCount(): Int = minOf(items.size, 4)
}
