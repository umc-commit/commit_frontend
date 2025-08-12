package com.example.commit.adapter.home

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.activity.author.AuthorProfileActivity
import com.example.commit.connection.RetrofitClient
import com.example.commit.databinding.ItemHomeCardBinding
import com.google.android.flexbox.FlexboxLayout

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
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder)
                .centerCrop()
                .into(binding.imageThumbnail)

            // 작가 프로필 이미지 + 닉네임
            binding.tvNickname.text = item.artist.nickname
            Glide.with(binding.root.context)
                .load(item.artist.profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.ivProfile)

            // 1. category + tags 조합
            val tagList = mutableListOf<String>()

            // 첫 번째 태그는 category (cyan 스타일)
            tagList.add(item.category)

            // 이후 tags (gray 스타일)
            tagList.addAll(item.tags)

            binding.tagsLayout.removeAllViews()

            tagList.forEachIndexed { index, tag ->
                val tagView = TextView(binding.root.context).apply {
                    text = tag
                    text = if (index == 0) tag else "#$tag"

                    if (index == 0) {
                        // category 태그: cyan 스타일
                        setTextColor(ContextCompat.getColor(context, R.color.mint1))
                        background = ContextCompat.getDrawable(context, R.drawable.tag_background_cyan)
                    } else {
                        // 나머지 tags: gray 스타일
                        setTextColor(ContextCompat.getColor(context, R.color.gray2))
                        background = ContextCompat.getDrawable(context, R.drawable.tag_background_gray)
                    }

                    includeFontPadding = false
                    textSize = 8f
                    setPadding(dpToPx(context, 6), dpToPx(context, 2), dpToPx(context, 6), dpToPx(context, 2))
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                    maxHeight = dpToPx(context, 16)
                    minWidth = dpToPx(context, 26)
                }
                val layoutParams = FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    // 마지막 태그가 아니면 간격 주기
                    if (index != tagList.lastIndex) {
                        setMargins(0, 0, dpToPx(binding.root.context, 4), 0)
                    }
                }

                tagView.layoutParams = layoutParams
                binding.tagsLayout.addView(tagView)
            }
            // 카드 클릭 이벤트
            binding.root.setOnClickListener { onItemClick(item) }

            binding.ivProfile.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, AuthorProfileActivity::class.java).apply {
                    putExtra("artistId", item.artist.id) // 작가 ID 등 필요한 데이터 전달
                    putExtra("nickname", item.artist.nickname)
                    putExtra("profileImageUrl", item.artist.profileImageUrl)
                }
                context.startActivity(intent)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        val binding = ItemHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCardAdapter.HomeCardViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)

    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}
