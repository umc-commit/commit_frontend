package com.example.commit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.databinding.ItemRequestBinding

class RequestsAdapter(private val requests: List<Request>) :
    RecyclerView.Adapter<RequestsAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(val binding: ItemRequestBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRequestBinding.inflate(inflater, parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val item = requests[position]
        holder.binding.apply {
            tvRequestTitle.text = item.title
            tvArtistNickname.text = item.artist.nickname
            tvRequestPrice.text = "${item.price}P"

            Glide.with(ivThumbnail.context)
                .load(item.thumbnailImage)
                .placeholder(R.drawable.default_title_image)
                .into(ivThumbnail)

            // 상태 바 처리
            requestProgressBar.progress = when (item.status) {
                "IN_PROGRESS" -> 50
                "DONE" -> 100
                else -> 0
            }

            tvRequestStatus.text = when (item.status) {
                "IN_PROGRESS" -> "진행 중"
                "DONE" -> "작업 완료"
                else -> "수락"
            }
        }
    }

    override fun getItemCount(): Int = requests.size
}
