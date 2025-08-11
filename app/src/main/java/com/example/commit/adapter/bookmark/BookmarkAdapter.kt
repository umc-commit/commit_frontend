package com.example.commit.adapter.bookmark

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient.BookmarkCommissionItem
import com.example.commit.databinding.ItemBookmarkCardBinding
import com.google.android.flexbox.FlexboxLayout

class BookmarkAdapter(
    private val items: MutableList<BookmarkCommissionItem>,
    private var isEditMode: Boolean,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.VH>() {

    private val selected = hashSetOf<Int>() // pos

    inner class VH(val b: ItemBookmarkCardBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: BookmarkCommissionItem, position: Int) {
            b.tvNickname.text = item.artist.nickname
            b.tvCourseTitle.text = item.title

            // --- 태그: HomeCardAdapter와 동일한 룰 ---
            val tagList = mutableListOf<String>().apply {
                val categoryName = item.category.name.orEmpty()
                if (categoryName.isNotBlank()) add(categoryName)
                addAll(item.tags.map { it.name })
            }

            b.tagsLayout.removeAllViews()
            tagList.forEachIndexed { index, tag ->
                val tv = TextView(b.root.context).apply {
                    text = if (index == 0) tag else "#$tag"
                    if (index == 0) {
                        setTextColor(ContextCompat.getColor(context, R.color.mint1))
                        background = ContextCompat.getDrawable(context, R.drawable.tag_background_cyan)
                    } else {
                        setTextColor(ContextCompat.getColor(context, R.color.gray2))
                        background = ContextCompat.getDrawable(context, R.drawable.tag_background_gray)
                    }
                    includeFontPadding = false
                    textSize = 8f
                    setPadding(dpToPx(context, 6), dpToPx(context, 2), dpToPx(context, 6), dpToPx(context, 2))
                    typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                    // 동일한 최소/최대 값으로 UI 정렬 (HomeCardAdapter 기준)
                    maxHeight = dpToPx(context, 16)
                    minWidth = dpToPx(context, 26)
                }
                val lp = FlexboxLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                    // 오른쪽/아래 여백으로 줄바꿈 시 간격 유지
                    setMargins(0, 0, dpToPx(b.root.context, 4), dpToPx(b.root.context, 4))
                }
                tv.layoutParams = lp
                b.tagsLayout.addView(tv)
            }
            // --------------------------------------

            Glide.with(b.root.context)
                .load(item.artist.profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .into(b.ivProfile)

            Glide.with(b.root.context)
                .load(item.thumbnailImageUrl)
                .placeholder(R.drawable.image_placeholder)
                .into(b.imageThumbnail)

            val soldOut = item.remainingSlots <= 0
            b.tvClosedLabel.visibility = if (soldOut) View.VISIBLE else View.GONE
            b.tvRemainingSlots.visibility = if (soldOut) View.GONE else View.VISIBLE
            if (!soldOut) b.tvRemainingSlots.text = "남은 슬롯 : ${item.remainingSlots}"

            // 편집모드 선택 아이콘
            b.ivBookmark.visibility = if (isEditMode) View.VISIBLE else View.GONE
            b.ivBookmark.setImageResource(
                if (selected.contains(position)) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_unchecked
            )

            b.root.setOnClickListener {
                if (!isEditMode) return@setOnClickListener
                if (selected.contains(position)) selected.remove(position) else selected.add(position)
                notifyItemChanged(position)
                onSelectionChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val infl = LayoutInflater.from(parent.context)
        return VH(ItemBookmarkCardBinding.inflate(infl, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position], position)
    override fun getItemCount(): Int = items.size

    fun setEditMode(edit: Boolean) {
        isEditMode = edit
        if (!edit) selected.clear()
        notifyDataSetChanged()
    }

    data class Selected(val commissionId: Int, val bookmarkId: Long?)
    fun getSelected(): List<Selected> =
        selected.map { pos -> Selected(items[pos].id, items[pos].bookmarkId) }

    fun getSelectedBookmarkIds(): List<Long>? {
        val ids = selected.map { items[it].bookmarkId }
        return if (ids.any { it == null }) null else ids.filterNotNull()
    }

    fun removeByBookmarkIds(deletedIds: List<Long>) {
        if (deletedIds.isEmpty()) return
        val set = deletedIds.toHashSet()
        val it = items.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item.bookmarkId != null && set.contains(item.bookmarkId)) {
                it.remove()
            }
        }
        selected.clear()
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selected.clear()
        notifyDataSetChanged()
    }

    private fun dpToPx(context: Context, dp: Int): Int =
        (context.resources.displayMetrics.density * dp).toInt()
}
