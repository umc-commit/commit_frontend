package com.example.commit.adapter.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFollowingAdapter(
    private var users: List<RetrofitClient.FollowedArtist>,
    private val onProfileClick: (artistId: String) -> Unit
) : RecyclerView.Adapter<ProfileFollowingAdapter.FollowingUserViewHolder>() {

    class FollowingUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: CircleImageView = itemView.findViewById(R.id.iv_profile)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val tvFollowerCount: TextView = itemView.findViewById(R.id.tv_follower_count)
        val btnProfile: Button = itemView.findViewById(R.id.btn_profile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following_user, parent, false)
        return FollowingUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowingUserViewHolder, position: Int) {
        val user = users[position]

        // 프로필 이미지
        if (!user.profileImage.isNullOrBlank()) {
            Glide.with(holder.itemView)
                .load(user.profileImage)
                .placeholder(R.drawable.ic_pf_charac2)
                .error(R.drawable.ic_pf_charac2)
                .into(holder.ivProfile)
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_pf_charac2)
        }

        // 닉네임
        holder.tvUsername.text = user.nickname

        // 서버 목록 총 개수를 팔로워 수처럼 표시
        holder.tvFollowerCount.text = "팔로워 ${users.size}"

        // 프로필 버튼 클릭 → 작가 프로필 화면 이동
        holder.btnProfile.setOnClickListener {
            onProfileClick(user.id)
        }
    }

    override fun getItemCount(): Int = users.size

    fun submit(newUsers: List<RetrofitClient.FollowedArtist>) {
        this.users = newUsers
        notifyDataSetChanged()
    }
}