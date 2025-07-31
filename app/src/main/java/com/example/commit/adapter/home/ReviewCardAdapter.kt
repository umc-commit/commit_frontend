package com.example.commit.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.databinding.ItemHomeReviewBinding

class ReviewCardAdapter(
    private val itemList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    inner class ReviewCardViewHolder(val binding: ItemHomeReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder {
        val binding = ItemHomeReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvReviewContent.text = item

        // 클릭 리스너 연결
        holder.binding.root.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)
}