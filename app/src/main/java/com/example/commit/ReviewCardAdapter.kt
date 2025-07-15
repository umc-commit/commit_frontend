package com.example.commit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.databinding.ItemHomeReviewBinding

class ReviewCardAdapter(
    private val itemList: List<String>
) : RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    inner class ReviewCardViewHolder(val binding: ItemHomeReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder {
        val binding = ItemHomeReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvReviewContent.text = item
    }

    override fun getItemCount(): Int = minOf(itemList.size, 4)
}
