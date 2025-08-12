package com.example.commit.adapter.author

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import android.util.TypedValue
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.flexbox.FlexboxLayout

class AuthorCommissionAdapter(
    private val items: MutableList<RetrofitClient.AuthorCommissionItem>
) : RecyclerView.Adapter<AuthorCommissionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDescription: TextView = view.findViewById(R.id.tv_description)
        val tagsLayout: FlexboxLayout = view.findViewById(R.id.tags_layout)
        val ivBookmark: ImageView = view.findViewById(R.id.iv_bookmark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_author_pf_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        Glide.with(holder.itemView.context)
            .load(R.drawable.image_placeholder) // 실제 이미지 필드가 추가되면 수정
            .placeholder(R.drawable.image_placeholder)
            .into(holder.thumbnail)

        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.summary

        // 태그 설정
        val context = holder.itemView.context
        holder.tagsLayout.removeAllViews()

        val tagList = mutableListOf<String>().apply {
            add(item.category)
            addAll(item.tags)
        }

        tagList.forEachIndexed { index, tag ->
            val tagView = TextView(context).apply {
                text = if (index == 0) tag else "#$tag"
                setTextColor(
                    ContextCompat.getColor(
                        context,
                        if (index == 0) R.color.mint1 else R.color.gray2
                    )
                )
                background = ContextCompat.getDrawable(
                    context,
                    if (index == 0) R.drawable.tag_background_cyan else R.drawable.tag_background_gray
                )
                textSize = 8f
                includeFontPadding = false
                gravity = Gravity.CENTER
                typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                setPadding(dpToPx(context, 6), dpToPx(context, 2), dpToPx(context, 6), dpToPx(context, 2))
                minWidth = dpToPx(context, 26)
                maxHeight = dpToPx(context, 16)
            }

            val layoutParams = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                if (index != tagList.lastIndex) {
                    setMargins(0, 0, dpToPx(context, 4), 0)
                }
            }

            tagView.layoutParams = layoutParams
            holder.tagsLayout.addView(tagView)
        }

        holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
        holder.ivBookmark.setOnClickListener {
            // 북마크 클릭 이벤트 (추후 서버 연동)
        }
    }

    private fun dpToPx(context: android.content.Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }

    override fun getItemCount(): Int = minOf(items.size, 6)
}
