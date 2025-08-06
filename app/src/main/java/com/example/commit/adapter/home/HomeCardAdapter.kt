package com.example.commit.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.databinding.ItemHomeCardBinding

class HomeCardAdapter(
    private val itemList: List<RetrofitClient.HomeCommissionItem>,
    private val onItemClick: (RetrofitClient.HomeCommissionItem) -> Unit
) : RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder>() {

    inner class HomeCardViewHolder(val binding: ItemHomeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RetrofitClient.HomeCommissionItem) {
            binding.tvCourseTitle.text = item.title

            Glide.with(binding.root.context)
                .load(item.thumbnailImageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.imageThumbnail)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        val binding = ItemHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)
}
