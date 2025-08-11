package com.example.commit.adapter.home

import android.content.Context
import android.content.Intent
import android.util.Log
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
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.ItemHomeCardBinding
import com.google.android.flexbox.FlexboxLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeCardAdapter(
    private val itemList: List<RetrofitClient.HomeCommissionItem>,
    private val onItemClick: (RetrofitClient.HomeCommissionItem) -> Unit
) : RecyclerView.Adapter<HomeCardAdapter.HomeCardViewHolder>() {

    companion object {
        private const val TAG = "HomeCardAdapter"
        private const val ACTION_BOOKMARK_CHANGED = "ACTION_BOOKMARK_CHANGED"
    }

    // 요청잠금 + 상태/식별자 캐시
    private val bookmarking = hashSetOf<Long>()         // 진행 중 commissionId
    private val bookmarked = hashSetOf<Long>()          // 세션 기준 on/off
    private val bookmarkIdMap = hashMapOf<Long, Long>() // commissionId -> bookmarkId

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

            binding.tvNickname.text = item.artist.nickname
            Glide.with(binding.root.context)
                .load(item.artist.profileImageUrl)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .circleCrop()
                .into(binding.ivProfile)

            // 태그
            val tagList = mutableListOf<String>().apply {
                add(item.category)
                addAll(item.tags)
            }
            binding.tagsLayout.removeAllViews()
            tagList.forEachIndexed { index, tag ->
                val tv = TextView(binding.root.context).apply {
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
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(context, R.font.notosanskr_medium)
                    maxHeight = dpToPx(context, 16)
                    minWidth = dpToPx(context, 26)
                }
                val lp = FlexboxLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { if (index != tagList.lastIndex) setMargins(0, 0, dpToPx(binding.root.context, 4), 0) }
                tv.layoutParams = lp
                binding.tagsLayout.addView(tv)
            }

            // 카드 클릭
            binding.root.setOnClickListener { onItemClick(item) }

            // 작가 프로필 진입
            binding.ivProfile.setOnClickListener {
                val context = binding.root.context
                context.startActivity(Intent(context, AuthorProfileActivity::class.java).apply {
                    putExtra("artistId", item.artist.id)
                    putExtra("nickname", item.artist.nickname)
                    putExtra("profileImageUrl", item.artist.profileImageUrl)
                })
            }

            // 북마크 아이콘 상태 (서버값 + 세션 캐시)
            val cId = item.id.toLong()
            if (item.isBookmarked) bookmarked.add(cId)
            binding.ivBookmark.setImageResource(
                if (bookmarked.contains(cId)) R.drawable.ic_home_bookmark_on else R.drawable.ic_home_bookmark
            )

            // 클릭 → 토글
            binding.ivBookmark.setOnClickListener {
                val ctx = binding.root.context
                if (bookmarking.contains(cId)) {
                    Log.d(TAG, "중복요청 차단: $cId"); return@setOnClickListener
                }
                val service = RetrofitObject.getRetrofitService(ctx)

                if (!bookmarked.contains(cId)) {
                    // ADD
                    bookmarking.add(cId); binding.ivBookmark.isEnabled = false
                    service.addBookmark(cId).enqueue(object :
                        Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>> {
                        override fun onResponse(
                            call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>,
                            resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkAddSuccess>>
                        ) {
                            bookmarking.remove(cId); binding.ivBookmark.isEnabled = true
                            val b = resp.body()
                            val ok = resp.isSuccessful && b?.resultType == "SUCCESS" && b.success != null
                            val already = resp.code() == 409 || b?.error?.reason?.contains("이미", true) == true
                            if (ok || already) {
                                b?.success?.bookmarkId?.let { bid -> bookmarkIdMap[cId] = bid }
                                bookmarked.add(cId)
                                binding.ivBookmark.setImageResource(R.drawable.ic_home_bookmark_on)
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
                            bookmarking.remove(cId); binding.ivBookmark.isEnabled = true
                            Log.d(TAG, "네트워크 오류(add): ${t.message}")
                        }
                    })
                } else {
                    // DELETE (bookmarkId 필요)
                    val bookmarkId = bookmarkIdMap[cId]
                    if (bookmarkId == null) {
                        Log.d(TAG, "bookmarkId 없음 → 삭제 불가 (추가할 때 받은 값이 없거나 앱 재시작)")
                        return@setOnClickListener
                    }
                    bookmarking.add(cId); binding.ivBookmark.isEnabled = false
                    service.deleteBookmark(cId, bookmarkId).enqueue(object :
                        Callback<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>> {
                        override fun onResponse(
                            call: Call<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>,
                            resp: Response<RetrofitClient.ApiResponse<RetrofitClient.BookmarkDeleteSuccess>>
                        ) {
                            bookmarking.remove(cId); binding.ivBookmark.isEnabled = true
                            val b = resp.body()
                            val ok = resp.isSuccessful && b?.resultType == "SUCCESS" && b.success != null
                            if (ok) {
                                bookmarked.remove(cId)
                                bookmarkIdMap.remove(cId)
                                binding.ivBookmark.setImageResource(R.drawable.ic_home_bookmark)
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
                            bookmarking.remove(cId); binding.ivBookmark.isEnabled = true
                            Log.d(TAG, "네트워크 오류(delete): ${t.message}")
                        }
                    })
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCardViewHolder {
        val binding = ItemHomeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeCardViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = minOf(itemList.size, 6)

    fun dpToPx(context: Context, dp: Int): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
}
