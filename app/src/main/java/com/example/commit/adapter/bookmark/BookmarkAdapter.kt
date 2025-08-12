package com.example.commit.adapter.bookmark

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.databinding.ItemBookmarkCardBinding
import com.example.commit.data.model.entities.BookmarkItem

class BookmarkAdapter(
    private val items: List<BookmarkItem>,
    private val isEditMode: Boolean,
    private val onItemChecked: (BookmarkItem, Boolean) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    inner class BookmarkViewHolder(val binding: ItemBookmarkCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BookmarkItem) {
            binding.tvNickname.text = item.nickname
            binding.tvCourseTitle.text = item.title

            // 태그 처리
            binding.tvTagDrawing.visibility = if (item.tags.contains("그림")) View.VISIBLE else View.GONE
            binding.tvTagLd.visibility = if (item.tags.contains("#LD")) View.VISIBLE else View.GONE
            binding.tvTagCouple.visibility = if (item.tags.contains("#커플")) View.VISIBLE else View.GONE

            // 프로필 & 썸네일 이미지
            Glide.with(binding.root.context)
                .load(item.profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivProfile)

            Glide.with(binding.root.context)
                .load(item.thumbnailUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(binding.imageThumbnail)

            // 마감 처리: isClosed == true 이거나 remainingSlots == 0 인 경우
            val isClosedOrSoldOut = item.isClosed || item.remainingSlots == 0

            if (isClosedOrSoldOut) {
                binding.tvClosedLabel.visibility = View.VISIBLE
                binding.tvRemainingSlots.visibility = View.GONE

                // #4D4D4D + 60% 불투명도 적용
                binding.imageThumbnail.foreground =
                    ColorDrawable(Color.parseColor("#994D4D4D"))
            } else {
                binding.tvClosedLabel.visibility = View.GONE
                binding.tvRemainingSlots.visibility = View.VISIBLE
                binding.tvRemainingSlots.text = "남은 슬롯 : ${item.remainingSlots}"

                // 오버레이 제거
                binding.imageThumbnail.foreground = null
            }

            // 선택 북마크 이미지 처리
            if (isEditMode) {
                binding.ivBookmark.visibility = View.VISIBLE
                binding.ivBookmark.setImageResource(
                    if (item.isSelected) R.drawable.ic_bookmark_checked
                    else R.drawable.ic_bookmark_unchecked
                )
            } else {
                binding.ivBookmark.visibility = View.GONE
            }

            // 클릭 이벤트
            binding.root.setOnClickListener {
                if (isEditMode) {
                    item.isSelected = !item.isSelected
                    notifyItemChanged(adapterPosition)
                    onItemChecked(item, item.isSelected)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getSelectedItems(): List<BookmarkItem> = items.filter { it.isSelected }

    fun clearSelection() {
        items.forEach { it.isSelected = false } // 모든 아이템 선택 해제
        notifyDataSetChanged()
    }
}
