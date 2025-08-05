package com.example.commit.repository

import android.content.Context
import android.util.Log
import com.example.commit.connection.RetrofitObject
import com.example.commit.data.model.CommissionDetail

object PostRepository {
    suspend fun getCommissionDetail(context: Context, id: Int): CommissionDetail? {
        return try {
            val service = RetrofitObject.getRetrofitService(context)
            Log.d("PostRepository", "API 호출 시작: id=$id")

            val response = service.getCommissionDetail(id)

            Log.d("PostRepository", "API 응답 resultType: ${response.resultType}")
            Log.d("PostRepository", "API 응답 success: ${response.success}")
            Log.d("PostRepository", "API 응답 error: ${response.error}")

            if (response.resultType == "SUCCESS") {
                response.success
            } else {
                Log.e("PostRepository", "API resultType이 SUCCESS가 아님: ${response.resultType}")
                null
            }
        } catch (e: Exception) {
            Log.e("PostRepository", "API 호출 중 예외 발생: ${e.message}", e)
            null
        }
    }
}
