package com.example.commit.adapter.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import com.example.commit.R
import com.example.commit.data.model.entities.FollowingUser

// RecyclerView 어댑터
class ProfileFollowingAdapter(private val users: List<FollowingUser>) :
    RecyclerView.Adapter<ProfileFollowingAdapter.FollowingUserViewHolder>() {

    // 각 아이템 뷰의 구성 요소를 담는 ViewHolder
    class FollowingUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfile: CircleImageView = itemView.findViewById(R.id.iv_profile)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val tvFollowerCount: TextView = itemView.findViewById(R.id.tv_follower_count)
        val btnProfile: Button = itemView.findViewById(R.id.btn_profile)
    }

    // ViewHolder 생성 시 호출
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingUserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following_user, parent, false) // item_following_user.xml 레이아웃 사용
        return FollowingUserViewHolder(view)
    }

    // ViewHolder에 데이터 바인딩 시 호출
    override fun onBindViewHolder(holder: FollowingUserViewHolder, position: Int) {
        val user = users[position]
        holder.ivProfile.setImageResource(user.profileImageResId) // 이미지 설정
        holder.tvUsername.text = user.username // 사용자 이름 설정
        holder.tvFollowerCount.text = "팔로워 ${user.followerCount}" // 팔로워 수 설정


        // "프로필" 버튼 클릭 리스너 (선택 사항)
        holder.btnProfile.setOnClickListener {
            // 버튼 클릭 시 동작 정의 (예: 토스트 메시지 표시)
            Toast.makeText(holder.itemView.context, "${user.username} 프로필 보기 또는 팔로우/언팔로우", Toast.LENGTH_SHORT).show()
        }
    }

    // 전체 아이템 개수 반환
    override fun getItemCount(): Int = users.size
}
