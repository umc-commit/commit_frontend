package com.example.commit.adapter.home

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingPostAdapter(
    private val postList: List<RetrofitClient.FollowingPostItem>,
    private val onMoreClick: () -> Unit,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<FollowingPostAdapter.FollowingPostViewHolder>() {

    companion object {
        private const val TAG = "FollowingPostAdapter"
        private const val ACTION_BOOKMARK_CHANGED = "ACTION_BOOKMARK_CHANGED"
    }

    // 북마크 상태/진행 플래그
    private val bookmarking = hashSetOf<Long>()
    private val bookmarked = hashSetOf<Long>()
    private val bookmarkIdMap = hashMapOf<Long, Long>()

    inner class FollowingPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.tv_user_name)
        val tvUserInfo: TextView = itemView.findViewById(R.id.tv_user_info)
        val tvPostTitle: TextView = itemView.findViewById(R.id.tv_post_title)
        val tvPostSummary: TextView = itemView.findViewById(R.id.tv_post_summary)
        val ivBookmark: ImageView = itemView.findViewById(R.id.iv_bookmark)
        val rvPostImages: RecyclerView = itemView.findViewById(R.id.rv_post_images)
        val divider: View = itemView.findViewById(R.id.view_divider)
        val ivMore: ImageView = itemView.findViewById(R.id.iv_more)
        val ivProfile: ImageView = itemView.findViewById(R.id.iv_profile)

        init {
            // 가로 썸네일 RecyclerView는 한 번만 초기화
            rvPostImages.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvPostImages.setHasFixedSize(true)
            rvPostImages.isNestedScrollingEnabled = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingPostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following_post, parent, false)
        return FollowingPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingPostViewHolder, position: Int) {
        val item = postList[position]
        val cId = item.id

        // 프로필/닉네임/시간
        holder.tvUserName.text = item.artist.nickname
        holder.tvUserInfo.text = "팔로워 ${item.artist.followCount} · ${item.timeAgo}"
        Glide.with(holder.itemView.context)
            .load(item.artist.profileImageUrl)
            .placeholder(R.drawable.ic_pf_charac2)
            .error(R.drawable.ic_pf_charac2)
            .into(holder.ivProfile)

        // 제목/요약
        holder.tvPostTitle.text = item.title
        holder.tvPostSummary.text = item.summary

        // 이미지 목록 (정렬 후 어댑터만 교체)
        val urls = item.images.sortedBy { it.orderIndex }.map { it.imageUrl }
        holder.rvPostImages.adapter = ImageListAdapter(urls)

        // 초기 북마크 아이콘
        if (item.isBookmarked) bookmarked.add(cId)
        holder.ivBookmark.setImageResource(
            if (bookmarked.contains(cId)) R.drawable.ic_home_bookmark_on
            else R.drawable.ic_home_bookmark
        )

        holder.ivMore.setOnClickListener { onMoreClick() }

        // 상세 진입
        val goDetail: (Long) -> Unit = { onItemClick(it) }
        holder.itemView.setOnClickListener { goDetail(cId) }
        holder.tvPostTitle.setOnClickListener { goDetail(cId) }
        holder.tvPostSummary.setOnClickListener { goDetail(cId) }

        // 북마크 토글
        holder.ivBookmark.setOnClickListener {
            val ctx = holder.itemView.context
            if (bookmarking.contains(cId)) {
                Log.d(TAG, "중복요청 차단: $cId")
                return@setOnClickListener
            }
            val service = RetrofitObject.getRetrofitService(ctx)

            if (!bookmarked.contains(cId)) {
                // ADD
                bookmarking.add(cId); holder.ivBookmark.isEnabled = false
                service.addBookmark(cId).enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                        resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>
                    ) {
                        bookmarking.remove(cId); holder.ivBookmark.isEnabled = true
                        val b = resp.body()
                        val ok = resp.isSuccessful && b?.resultType == "SUCCESS" && b.success != null
                        val already = resp.code() == 409 || b?.error?.reason?.contains("이미", true) == true
                        if (ok || already) {
                            b?.success?.bookmarkId?.let { bid -> bookmarkIdMap[cId] = bid }
                            if (already && !bookmarkIdMap.containsKey(cId)) {
                                resolveBookmarkId(ctx, cId) { bid ->
                                    if (bid != null) bookmarkIdMap[cId] = bid
                                }
                            }
                            bookmarked.add(cId)
                            holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark_on)
                            ctx.sendBroadcast(Intent(ACTION_BOOKMARK_CHANGED).setPackage(ctx.packageName))
                        } else {
                            Log.d(TAG, b?.error?.reason ?: "북마크 추가 실패")
                        }
                    }
                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                        t: Throwable
                    ) {
                        bookmarking.remove(cId); holder.ivBookmark.isEnabled = true
                        Log.d(TAG, "네트워크 오류(add): ${t.message}")
                    }
                })
            } else {
                // DELETE
                val bookmarkId = bookmarkIdMap[cId]
                if (bookmarkId == null) {
                    resolveBookmarkId(ctx, cId) { bid ->
                        if (bid == null) return@resolveBookmarkId
                        bookmarkIdMap[cId] = bid
                        bookmarking.add(cId); holder.ivBookmark.isEnabled = false
                        service.deleteBookmark(cId, bid).enqueue(object :
                            Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>> {
                            override fun onResponse(
                                call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                                resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>
                            ) {
                                bookmarking.remove(cId); holder.ivBookmark.isEnabled = true
                                val ok2 = resp.isSuccessful && resp.body()?.success != null
                                if (ok2) {
                                    bookmarked.remove(cId)
                                    bookmarkIdMap.remove(cId)
                                    holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
                                    ctx.sendBroadcast(Intent(ACTION_BOOKMARK_CHANGED).setPackage(ctx.packageName))
                                }
                            }
                            override fun onFailure(
                                call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                                t: Throwable
                            ) {
                                bookmarking.remove(cId); holder.ivBookmark.isEnabled = true
                            }
                        })
                    }
                } else {
                    bookmarking.add(cId); holder.ivBookmark.isEnabled = false
                    service.deleteBookmark(cId, bookmarkId).enqueue(object :
                        Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>> {
                        override fun onResponse(
                            call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                            resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>
                        ) {
                            bookmarking.remove(cId); holder.ivBookmark.isEnabled = true
                            val ok = resp.isSuccessful && resp.body()?.success != null
                            if (ok) {
                                bookmarked.remove(cId)
                                bookmarkIdMap.remove(cId)
                                holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
                                ctx.sendBroadcast(Intent(ACTION_BOOKMARK_CHANGED).setPackage(ctx.packageName))
                            }
                        }
                        override fun onFailure(
                            call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                            t: Throwable
                        ) {
                            bookmarking.remove(cId); holder.ivBookmark.isEnabled = true
                        }
                    })
                }
            }
        }

        // 마지막 아이템 구분선 숨김
        holder.divider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
    }

    override fun onViewRecycled(holder: FollowingPostViewHolder) {
        super.onViewRecycled(holder)
        // 프로필 이미지 clear
        Glide.with(holder.itemView.context).clear(holder.ivProfile)
        // 내부 썸네일 clear (현재 구조에서 자식 뷰의 ImageView를 찾아 정리)
        val count = holder.rvPostImages.childCount
        for (i in 0 until count) {
            val child = holder.rvPostImages.getChildAt(i)
            val iv = child?.findViewById<ImageView>(R.id.iv_thumbnail)
            if (iv != null) Glide.with(holder.itemView.context).clear(iv)
        }
    }

    override fun getItemCount(): Int = postList.size

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
                ) {
                    onResult(null)
                }
            })
    }
}