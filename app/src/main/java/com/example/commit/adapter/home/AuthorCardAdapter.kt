package com.example.commit.adapter.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.activity.author.AuthorProfileActivity
import com.example.commit.connection.RetrofitClient
import com.example.commit.databinding.ItemAuthorProfileBinding

class AuthorCardAdapter(
    private val itemList: List<RetrofitClient.HomeAuthorItem>
) : RecyclerView.Adapter<AuthorCardAdapter.AuthorCardViewHolder>() {

    inner class AuthorCardViewHolder(val binding: ItemAuthorProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RetrofitClient.HomeAuthorItem) {
            binding.tvAuthorNickname.text = item.nickname

            Glide.with(binding.root.context)
                .load(item.profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(binding.ivAuthorProfile)

            itemView.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, AuthorProfileActivity::class.java)
                intent.putExtra("artistId", item.id) // 작가 ID 전달 (HomeAuthorItem.id)
                context.startActivity(intent)
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorCardViewHolder {
        val binding = ItemAuthorProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AuthorCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AuthorCardViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)
}
