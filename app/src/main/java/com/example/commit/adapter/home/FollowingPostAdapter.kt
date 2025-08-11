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
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingPostAdapter(
    private val postList: List<String>,                  // TODO: 실제 Post 모델로 교체 예정
    private val onMoreClick: () -> Unit,
    private val commissionIdProvider: (position: Int) -> Long? = { null } // 기본값
) : RecyclerView.Adapter<FollowingPostAdapter.FollowingPostViewHolder>() {

    companion object {
        private const val TAG = "FollowingPostAdapter"
        private const val ACTION_BOOKMARK_CHANGED = "ACTION_BOOKMARK_CHANGED"
    }

    // 세션 캐시
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingPostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_following_post, parent, false)
        return FollowingPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingPostViewHolder, position: Int) {
        // 예시 바인딩
        holder.tvUserName.text = "로지"
        holder.tvUserInfo.text = "팔로워 30 · 3시간 전"
        holder.tvPostTitle.text = "커미션 타임글 제목"
        holder.tvPostSummary.text = "타입글 한줄 요약"

        val cId = commissionIdProvider(position)
        holder.ivBookmark.setImageResource(
            if (cId != null && bookmarked.contains(cId)) R.drawable.ic_home_bookmark_on else R.drawable.ic_home_bookmark
        )

        holder.ivBookmark.setOnClickListener {
            val ctx = holder.itemView.context
            val commissionId = commissionIdProvider(position)
            if (commissionId == null) {
                Log.d(TAG, "commissionId를 알 수 없어 북마크 처리 불가 (pos=$position)")
                return@setOnClickListener
            }
            if (bookmarking.contains(commissionId)) {
                Log.d(TAG, "중복요청 차단: $commissionId"); return@setOnClickListener
            }
            val service = RetrofitObject.getRetrofitService(ctx)

            if (!bookmarked.contains(commissionId)) {
                // ADD
                bookmarking.add(commissionId); holder.ivBookmark.isEnabled = false
                service.addBookmark(commissionId).enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                        resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>
                    ) {
                        bookmarking.remove(commissionId); holder.ivBookmark.isEnabled = true
                        val b = resp.body()
                        val ok = resp.isSuccessful && b?.resultType == "SUCCESS" && b.success != null
                        val already = resp.code() == 409 || b?.error?.reason?.contains("이미", true) == true
                        if (ok || already) {
                            b?.success?.bookmarkId?.let { bid -> bookmarkIdMap[commissionId] = bid }
                            bookmarked.add(commissionId)
                            holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark_on)
                            Log.d(TAG, b?.success?.message ?: "북마크 추가됨")
                            ctx.sendBroadcast(Intent("ACTION_BOOKMARK_CHANGED").setPackage(ctx.packageName))
                        } else {
                            Log.d(TAG, b?.error?.reason ?: "북마크 추가 실패")
                        }
                    }
                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                        t: Throwable
                    ) {
                        bookmarking.remove(commissionId); holder.ivBookmark.isEnabled = true
                        Log.d(TAG, "네트워크 오류(add): ${t.message}")
                    }
                })
            } else {
                // DELETE (bookmarkId 필요)
                val bookmarkId = bookmarkIdMap[commissionId]
                if (bookmarkId == null) {
                    Log.d(TAG, "bookmarkId 없음 → 삭제 불가"); return@setOnClickListener
                }
                bookmarking.add(commissionId); holder.ivBookmark.isEnabled = false
                service.deleteBookmark(commissionId, bookmarkId).enqueue(object :
                    Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>> {
                    override fun onResponse(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                        resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>
                    ) {
                        bookmarking.remove(commissionId); holder.ivBookmark.isEnabled = true
                        val b = resp.body()
                        val ok = resp.isSuccessful && b?.resultType == "SUCCESS" && b.success != null
                        if (ok) {
                            bookmarked.remove(commissionId)
                            bookmarkIdMap.remove(commissionId)
                            holder.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
                            Log.d(TAG, b!!.success!!.message)
                            ctx.sendBroadcast(Intent("ACTION_BOOKMARK_CHANGED").setPackage(ctx.packageName))
                        } else {
                            Log.d(TAG, b?.error?.reason ?: "북마크 삭제 실패")
                        }
                    }
                    override fun onFailure(
                        call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                        t: Throwable
                    ) {
                        bookmarking.remove(commissionId); holder.ivBookmark.isEnabled = true
                        Log.d(TAG, "네트워크 오류(delete): ${t.message}")
                    }
                })
            }
        }

        // 이미지 리스트 (예시)
        val imageList = List(minOf(10, position + 2)) { R.drawable.image_placeholder }
        holder.rvPostImages.apply {
            adapter = ImageListAdapter(imageList)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        holder.divider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE
        holder.ivMore.setOnClickListener { onMoreClick() }
    }

    override fun getItemCount(): Int = postList.size
}
