package com.example.commit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.databinding.ItemAuthorProfileBinding

class AuthorCardAdapter(
    private val itemList: List<String>
) : RecyclerView.Adapter<AuthorCardAdapter.AuthorCardViewHolder>() {

    inner class AuthorCardViewHolder(val binding: ItemAuthorProfileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorCardViewHolder {
        val binding = ItemAuthorProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuthorCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuthorCardViewHolder, position: Int) {
        val item = itemList[position]
        holder.binding.tvAuthorNickname.text = item
    }

    override fun getItemCount(): Int = minOf(itemList.size, 4)
}
