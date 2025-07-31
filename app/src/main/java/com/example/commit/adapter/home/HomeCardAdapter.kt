package com.example.commit.adapter.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.databinding.ItemHomeCardBinding

class HomeCardAdapter(
    private val itemList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder>() {

    inner class HomeCardViewHolder(val binding: ItemHomeCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvCourseTitle.text = item
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        val binding = ItemHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)
}
