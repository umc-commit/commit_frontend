package com.example.commit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.databinding.ItemHomeCardBinding

class HomeCardAdapter(
    private val itemList: List<String>
) : RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder>() {

    inner class HomeCardViewHolder(val binding: ItemHomeCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        val binding = ItemHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvCourseTitle.text = item
        // 다른 데이터 세팅 가능
    }

    override fun getItemCount(): Int = minOf(itemList.size, 4)
}
