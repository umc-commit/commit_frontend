package com.example.commit.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.databinding.ItemHomeReviewBinding

class ReviewCardAdapter(
    private val itemList: List<RetrofitClient.HomeReviewItem>,
    private val onItemClick: (RetrofitClient.HomeReviewItem) -> Unit
) : RecyclerView.Adapter<ReviewCardAdapter.ReviewCardViewHolder>() {

    inner class ReviewCardViewHolder(val binding: ItemHomeReviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RetrofitClient.HomeReviewItem) {
            binding.tvReviewContent.text = item.content

            Glide.with(binding.root.context)
                .load(item.reviewImageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.ivReview)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewCardViewHolder {
        val binding = ItemHomeReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewCardViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)
}