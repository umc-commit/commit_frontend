package com.example.commit.adapter.author

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.google.android.flexbox.FlexboxLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthorCommissionAdapter(
    private val items: MutableList<RetrofitClient.AuthorCommissionItem>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AuthorCommissionAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "AuthorCommissionAdapter"
        private const val ACTION_BOOKMARK_CHANGED = "ACTION_BOOKMARK_CHANGED"
    }

    // 요청 잠금 및 상태/식별자 캐시 (ViewHolder 밖으로 이동)
    private val bookmarking = hashSetOf<Long>()         // 요청 중인 commissionId
    private val bookmarked = hashSetOf<Long>()          // 세션 기준 on/off
    private val bookmarkIdMap = hashMapOf<Long, Long>() // commissionId -> bookmarkId

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

        // 카드/썸네일/텍스트 클릭 시 상세로 이동
        val idInt = item.id.toIntOrNull()
        val fireClick: (() -> Unit)? = idInt?.let { cid -> { onItemClick(cid) } }

        holder.itemView.setOnClickListener { fireClick?.invoke() }
        holder.thumbnail.setOnClickListener { fireClick?.invoke() }
        holder.tvTitle.setOnClickListener { fireClick?.invoke() }
        holder.tvDescription.setOnClickListener { fireClick?.invoke() }

        // 썸네일
        val imgUrl = item.commission_img?.takeIf { it.isNotBlank() }
        Glide.with(holder.itemView.context)
            .load(imgUrl ?: R.drawable.image_placeholder)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .centerCrop()
            .into(holder.thumbnail)
        holder.thumbnail.contentDescription = item.title

        // 텍스트
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.summary

        // 태그
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
                        context, if (index == 0) R.color.mint1 else R.color.gray2
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
                setPadding(
                    dpToPx(context, 6),
                    dpToPx(context, 2),
                    dpToPx(context, 6),
                    dpToPx(context, 2)
                )
                minWidth = dpToPx(context, 26)
                maxHeight = dpToPx(context, 16)
            }
            val lp = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                if (index != tagList.lastIndex) setMargins(0, 0, dpToPx(context, 4), 0)
            }
            tagView.layoutParams = lp
            holder.tagsLayout.addView(tagView)
        }

        // 초기 북마크 아이콘 (서버 값 기반으로 세팅)
        val cId = item.id.toLongOrNull()
        val initiallyOn = (cId != null && item.isBookmarked)
        if (initiallyOn) cId?.let { bookmarked.add(it) }

        holder.ivBookmark.setImageResource(
            if (initiallyOn) R.drawable.ic_home_bookmark_on else R.drawable.ic_home_bookmark
        )

        // 북마크 아이콘: 부모 인터셉트 방지 + z-order 보정
        holder.ivBookmark.setOnTouchListener { v, _ ->
            (v.parent as? ViewGroup)?.requestDisallowInterceptTouchEvent(true)
            false
        }
        holder.ivBookmark.bringToFront() // 겹침 케이스일 때만 효과

        // 클릭 리스너
        holder.ivBookmark.setOnClickListener {
            val ctx = holder.itemView.context
            val commissionId = item.id.toLongOrNull()
            if (commissionId == null) {
                Log.d(TAG, "잘못된 commissionId: ${item.id}")
                return@setOnClickListener
            }
            if (bookmarking.contains(commissionId)) {
                Log.d(TAG, "중복 요청 차단: $commissionId")
                return@setOnClickListener
            }

            val service = RetrofitObject.getRetrofitService(ctx)

            // OFF -> ON : 추가
            if (!bookmarked.contains(commissionId)) {
                bookmarking.add(commissionId)
                holder.ivBookmark.isEnabled = false

                service.addBookmark(commissionId).enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                        resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>
                    ) {
                        bookmarking.remove(commissionId)
                        holder.ivBookmark.isEnabled = true

                        val body = resp.body()
                        val ok =
                            resp.isSuccessful && body?.resultType == "SUCCESS" && body.success != null
                        val already =
                            resp.code() == 409 || body?.error?.reason?.contains(
                                "이미",
                                ignoreCase = true
                            ) == true

                        if (ok || already) {
                            val bookmarkId = body?.success?.bookmarkId
                            if (bookmarkId != null) bookmarkIdMap[commissionId] = bookmarkId
                            if (already && !bookmarkIdMap.containsKey(commissionId)) {
                                resolveBookmarkId(ctx, commissionId) { bid ->
                                    if (bid != null) bookmarkIdMap[commissionId] = bid
                                }
                            }
                            bookmarked.add(commissionId)
                            holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark_on)
                            ctx.sendBroadcast(Intent("ACTION_BOOKMARK_CHANGED").setPackage(ctx.packageName))
                        } else {
                            Log.d(TAG, body?.error?.reason ?: "북마크 추가 실패")
                        }
                    }

                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                        t: Throwable
                    ) {
                        bookmarking.remove(commissionId)
                        holder.ivBookmark.isEnabled = true
                        Log.d(TAG, "네트워크 오류(add): ${t.message}")
                    }
                })
            } else {
                // ON -> OFF : 삭제
                val bookmarkId = bookmarkIdMap[commissionId]
                if (bookmarkId == null) {
                    resolveBookmarkId(ctx, commissionId) { bid ->
                        if (bid == null) return@resolveBookmarkId
                        bookmarkIdMap[commissionId] = bid
                        bookmarking.add(commissionId)
                        holder.ivBookmark.isEnabled = false
                        service.deleteBookmark(commissionId, bid).enqueue(object :
                            Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>> {
                            override fun onResponse(
                                call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                                resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>
                            ) {
                                bookmarking.remove(commissionId)
                                holder.ivBookmark.isEnabled = true
                                val ok2 = resp.isSuccessful && resp.body()?.success != null
                                if (ok2) {
                                    bookmarked.remove(commissionId)
                                    bookmarkIdMap.remove(commissionId)
                                    holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
                                    ctx.sendBroadcast(
                                        Intent("ACTION_BOOKMARK_CHANGED").setPackage(
                                            ctx.packageName
                                        )
                                    )
                                }
                            }

                            override fun onFailure(
                                call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                                t: Throwable
                            ) {
                                bookmarking.remove(commissionId)
                                holder.ivBookmark.isEnabled = true
                            }
                        })
                    }
                    return@setOnClickListener
                } else {
                    bookmarking.add(commissionId)
                    holder.ivBookmark.isEnabled = false
                    service.deleteBookmark(commissionId, bookmarkId).enqueue(object :
                        Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>> {
                        override fun onResponse(
                            call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                            resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>
                        ) {
                            bookmarking.remove(commissionId)
                            holder.ivBookmark.isEnabled = true
                            val ok = resp.isSuccessful && resp.body()?.success != null
                            if (ok) {
                                bookmarked.remove(commissionId)
                                bookmarkIdMap.remove(commissionId)
                                holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
                                ctx.sendBroadcast(Intent("ACTION_BOOKMARK_CHANGED").setPackage(ctx.packageName))
                            }
                        }

                        override fun onFailure(
                            call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                            t: Throwable
                        ) {
                            bookmarking.remove(commissionId)
                            holder.ivBookmark.isEnabled = true
                        }
                    })
                }
            }
        }
    }

    override fun getItemCount(): Int = minOf(items.size, 6)

    // ---- utils ----
    private fun dpToPx(context: android.content.Context, dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics
        ).toInt()

    private fun resolveBookmarkId(
        ctx: android.content.Context,
        commissionId: Long,
        onResult: (Long?) -> Unit
    ) {
        val service = RetrofitObject.getRetrofitService(ctx)
        service.getBookmarks(page = 1, limit = 200, excludeFullSlots = false)
            .enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>> {
                override fun onResponse(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>,
                    response: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>
                ) {
                    val items = response.body()?.success?.items.orEmpty()
                    val bid = items.firstOrNull { it.id.toLong() == commissionId }?.bookmarkId
                    onResult(bid)
                }
                override fun onFailure(
                    call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkListSuccess>>,
                    t: Throwable
                ) { onResult(null) }
            })
    }

}
