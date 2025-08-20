package com.example.commit.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.commit.R
import com.example.commit.connection.RetrofitClient
import com.example.commit.connection.RetrofitObject
import com.example.commit.fragment.FragmentPostChatDetail
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ChatroomUtils {
    
    /**
     * 공통 채팅방 생성 메서드
     * PostScreen과 ChatList에서 동일하게 사용
     */
    fun createChatroom(
        context: Context,
        fragmentManager: FragmentManager,
        commissionId: Int,
        commissionTitle: String,
        artistId: Int,
        sourceFragment: String,
        onSuccess: ((Int) -> Unit)? = null,
        onFailure: ((String) -> Unit)? = null
    ) {
        Log.d("ChatroomUtils", "createChatroom 호출 - commissionId: $commissionId, artistId: $artistId, title: $commissionTitle")
        
        val api = RetrofitObject.getRetrofitService(context)
        
        val request = RetrofitClient.CreateChatroomRequest(
            artistId = artistId,
            commissionId = commissionId.toString()
        )
        
        api.createChatroom(request).enqueue(object :
            Callback<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>> {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>
            ) {
                if (!response.isSuccessful) {
                    val errorMsg = "채팅방 생성 실패 - code=${response.code()}"
                    Log.e("ChatroomUtils", errorMsg)
                    onFailure?.invoke(errorMsg)
                    return
                }
                
                val data = response.body()?.success
                if (data == null) {
                    val errorMsg = "채팅방 생성 실패 - 응답 없음"
                    Log.e("ChatroomUtils", errorMsg)
                    onFailure?.invoke(errorMsg)
                    return
                }
                
                val chatroomIdInt = data.id.toIntOrNull()
                if (chatroomIdInt == null) {
                    val errorMsg = "채팅방 ID 변환 실패"
                    Log.e("ChatroomUtils", errorMsg)
                    onFailure?.invoke(errorMsg)
                    return
                }
                
                // 작가 닉네임 조회
                api.getCommissionArtist(commissionId.toString()).enqueue(object :
                    Callback<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>> {
                    override fun onResponse(
                        call: Call<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>>,
                        resp: Response<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>>
                    ) {
                        val nickname = resp.body()?.success?.artist?.nickname ?: ""
                        
                        // 채팅 화면 진입
                        navigateToChatroom(
                            fragmentManager = fragmentManager,
                            chatName = commissionTitle,
                            authorName = nickname,
                            chatroomId = chatroomIdInt,
                            sourceFragment = sourceFragment,
                            commissionId = commissionId,
                            artistId = artistId
                        )
                        
                        onSuccess?.invoke(chatroomIdInt)
                        Toast.makeText(context, "채팅방이 생성되었습니다", Toast.LENGTH_SHORT).show()
                    }
                    
                    override fun onFailure(
                        call: Call<com.example.commit.connection.dto.ApiResponse<com.example.commit.connection.dto.CommissionArtistResponse>>,
                        t: Throwable
                    ) {
                        Log.e("ChatroomUtils", "작가 조회 실패: ${t.message}")
                        
                        // 실패해도 화면은 진입하되, authorName은 빈 값
                        navigateToChatroom(
                            fragmentManager = fragmentManager,
                            chatName = commissionTitle,
                            authorName = "",
                            chatroomId = chatroomIdInt,
                            sourceFragment = sourceFragment,
                            commissionId = commissionId,
                            artistId = artistId
                        )
                        
                        onSuccess?.invoke(chatroomIdInt)
                        Toast.makeText(context, "채팅방이 생성되었습니다", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            
            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.CreateChatroomResponse>>,
                t: Throwable
            ) {
                val errorMsg = "채팅방 생성 네트워크 오류: ${t.message}"
                Log.e("ChatroomUtils", errorMsg, t)
                onFailure?.invoke(errorMsg)
            }
        })
    }
    
    /**
     * 채팅방 화면으로 이동
     */
    private fun navigateToChatroom(
        fragmentManager: FragmentManager,
        chatName: String,
        authorName: String,
        chatroomId: Int,
        sourceFragment: String,
        commissionId: Int,
        artistId: Int
    ) {
        val fragment = FragmentPostChatDetail().apply {
            arguments = android.os.Bundle().apply {
                putString("chatName", chatName)
                putString("authorName", authorName)
                putInt("chatroomId", chatroomId)
                putString("sourceFragment", sourceFragment)
                putInt("commissionId", commissionId)
                putInt("artistId", artistId)
            }
        }
        
        fragmentManager.beginTransaction()
            .replace(R.id.Nav_Frame, fragment)
            .addToBackStack(null)
            .commit()
    }
}
