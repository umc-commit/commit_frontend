package com.example.commit.connection.dto

import android.content.Context
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FollowHelper {
    const val ACTION_FOLLOW_CHANGED = "commit.ACTION_FOLLOW_CHANGED"
    const val EXTRA_ARTIST_ID = "artistId"
    const val EXTRA_IS_FOLLOWING = "isFollowing"
    const val EXTRA_FOLLOWER_COUNT = "followerCount"

    fun toggleFollow(
        context: Context,
        artistId: Int,
        follow: Boolean,
        onComplete: (success: Boolean, nowFollowing: Boolean, serverMessage: String?) -> Unit
    ) {
        val api = RetrofitObject.getRetrofitService(context) // Authorization 헤더 자동 주입됨
        val call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>> =
            if (follow) api.followArtist(artistId) else api.unfollowArtist(artistId)

        call.enqueue(object : Callback<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>
            ) {
                if (response.isSuccessful) {
                    val msg = response.body()?.success?.message
                    onComplete(true, follow, msg)
                } else {
                    onComplete(false, !follow, response.errorBody()?.string())
                }
            }

            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FollowSuccess>>,
                t: Throwable
            ) {
                onComplete(false, !follow, t.message)
            }
        })
    }
}
