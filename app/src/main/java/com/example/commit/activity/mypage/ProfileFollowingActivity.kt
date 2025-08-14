package com.example.commit.activity.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.commit.adapter.mypage.ProfileFollowingAdapter
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.databinding.ActivityProfileFollowingBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFollowingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileFollowingBinding
    private lateinit var adapter: ProfileFollowingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        // RecyclerView 기본 세팅
        adapter = ProfileFollowingAdapter(
            users = emptyList(),
            onProfileClick = { artistId ->
                // 👉 AuthorProfileActivity로 이동 (패키지/클래스명은 실제 프로젝트에 맞게 수정)
                try {
                    val intent = Intent(this, Class.forName("com.example.commit.activity.author.AuthorProfileActivity"))
                    intent.putExtra("artistId", artistId) // Int로 쓰고 싶으면 여기서 toInt()
                    startActivity(intent)
                } catch (e: ClassNotFoundException) {
                    Toast.makeText(this, "AuthorProfileActivity를 찾을 수 없어요. 경로를 확인해주세요.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvFollowingList.apply {
            layoutManager = LinearLayoutManager(this@ProfileFollowingActivity)
            adapter = this@ProfileFollowingActivity.adapter
        }

        fetchFollowingArtists()
    }

    private fun fetchFollowingArtists() {
        // 로딩 표시가 필요하면 여기서 보여주기
        val service = RetrofitObject.getRetrofitService(this)
        service.getFollowedArtists().enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.FollowedArtistsSuccess>> {

            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowedArtistsSuccess>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.FollowedArtistsSuccess>>
            ) {
                val body = response.body()
                val items = body?.success?.artistList?.map { it.artist }.orEmpty()
                adapter.submit(items)
                // 로딩 숨기기
                if (items.isEmpty()) {
                    Toast.makeText(this@ProfileFollowingActivity, "팔로우한 작가가 없어요.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowedArtistsSuccess>>,
                t: Throwable
            ) {
                Toast.makeText(this@ProfileFollowingActivity, "목록을 불러오지 못했어요.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
