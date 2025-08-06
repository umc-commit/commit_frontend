package com.example.commit.adapter.author

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AuthorReviewAdapter(
    private val items: List<RetrofitClient.AuthorReviewItem>
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

        holder.tvRating.text = String.format("%.1f", item.rate)
        holder.tvCommissionName.text = item.commissionTitle
        holder.tvReviewContent.text = item.content
        holder.tvReviewer.text = item.writer.nickname
        holder.tvTime.text = formatTimeAgo(item.createdAt)
        holder.tvWorkPeriod.text = "작업기간 : ${item.workingTime ?: "-"}"
    }

    private fun formatTimeAgo(isoDate: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(isoDate)
            val diff = System.currentTimeMillis() - (date?.time ?: 0L)
            val days = diff / (1000 * 60 * 60 * 24)
            when {
                days > 0 -> "${days}일 전"
                else -> "오늘"
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    override fun getItemCount(): Int = minOf(items.size, 6)
}
