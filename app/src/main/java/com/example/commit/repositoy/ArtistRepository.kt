package com.example.commit.repository

import android.content.Context
import android.util.Log
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.ArtistSuccessData

object ArtistRepository {

    suspend fun getArtistInfo(context: Context, commissionId: Int): ArtistSuccessData? {
        return try {
            val service = RetrofitObject.getRetrofitService(context)
            Log.d("ArtistRepository", "API 요청 시작: commissionId = $commissionId")

            val response = service.getArtistInfo(commissionId)

            if (response.isSuccessful) {
                val result = response.body()?.success
                Log.d("ArtistRepository", "API 응답 성공: nickname = ${result?.artist?.nickname}")
                result
            } else {
                Log.e(
                    "ArtistRepository",
                    "API 응답 실패: code=${response.code()}, error=${response.errorBody()?.string()}"
                )
                null
            }
        } catch (e: Exception) {
            Log.e("ArtistRepository", "API 요청 중 예외 발생: ${e.message}", e)
            null
        }
    }
}
