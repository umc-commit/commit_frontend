package com.example.commit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.commit.R

class FollowingPostAdapter(
    private val postList: List<String>,  // 예시용 데이터(추후 Post 모델로 대체)
    private val onMoreClick: () -> Unit
) : RecyclerView.Adapter<FollowingPostAdapter.FollowingPostViewHolder>() {

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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following_post, parent, false)
        return FollowingPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingPostViewHolder, position: Int) {
        val item = postList[position]

        // 예시 바인딩 – 실제 앱에서는 Post 모델에 따라 바꾸세요
        holder.tvUserName.text = "로지"
        holder.tvUserInfo.text = "팔로워 30 · 3시간 전"
        holder.tvPostTitle.text = "커미션 타임글 제목"
        holder.tvPostSummary.text = "타입글 한줄 요약"

        // 북마크 버튼 클릭 이벤트 (예시)
        holder.ivBookmark.setOnClickListener {
            // TODO: 북마크 토글 구현
        }

        // 이미지 리스트 – 최대 10개 제한 (예시용 동일 이미지 사용)
        val imageList = List(minOf(10, position + 2)) { R.drawable.image_placeholder }

        holder.rvPostImages.apply {
            adapter = ImageListAdapter(imageList)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        // 마지막 아이템이면 divider 숨기기
        holder.divider.visibility = if (position == itemCount - 1) View.GONE else View.VISIBLE

        holder.ivMore.setOnClickListener {
            onMoreClick()
        }
    }

    override fun getItemCount(): Int = postList.size
}
