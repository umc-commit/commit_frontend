package com.example.commit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.R
import com.example.commit.adapter.ProfileFollowingAdapter
import com.example.commit.data.model.entities.FollowingUser
import com.example.commit.databinding.ActivityProfileFollowingBinding

class ProfileFollowingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileFollowingBinding // ViewBinding 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewBinding 초기화
        binding = ActivityProfileFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로가기 버튼 클릭 리스너 설정
        binding.ivBack.setOnClickListener {
            finish()   // 이전 화면으로 돌아가기
        }

        // 팔로잉 사용자 더미 데이터 생성 (3개)
        val followingUsers = listOf(
            FollowingUser(R.drawable.ic_pf_charac, "키르", 32, true), // 예시 이미지, 이름, 팔로워 수, 팔로잉 여부
            FollowingUser(R.drawable.ic_pf_charac, "곤", 15, true),
            FollowingUser(R.drawable.ic_pf_charac, "레오리오", 20, true)
        )

        // RecyclerView 설정
        binding.rvFollowingList.apply {
            // 레이아웃 매니저 설정 (수직 스크롤 목록)
            layoutManager = LinearLayoutManager(this@ProfileFollowingActivity)
            // 어댑터 설정
            adapter = ProfileFollowingAdapter(followingUsers)
        }
    }
}
