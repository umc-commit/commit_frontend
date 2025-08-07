package com.example.commit.connection

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitObject {

    private const val BASE_URL = "https://commit.n-e.kr"

    // 토큰을 SharedPreferences에서 가져오기

    private fun getAccessToken(context: Context): String? {
        val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
        return prefs.getString("accessToken", null)
    }
    

    /*private fun getAccessToken(context: Context): String? {
        // 테스트용 임시 토큰 고정
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxIiwibmlja25hbWUiOiJ1c2VyX29uZSIsImFjY291bnRJZCI6IjEiLCJwcm92aWRlciI6Imtha2FvIiwiaWF0IjoxNzU0NTQyNDYzLCJleHAiOjE3NTQ1NDMwNjN9.gKBu9UVvJMEVD_6fic9T0nGzUuZqhmmgCVou8GwkaQ8"
    }*/


    fun getRetrofitService(context: Context): RetrofitAPI {
        // 토큰 자동 추가 인터셉터
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            val token = getAccessToken(context)
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor) // 토큰 자동 추가
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitAPI::class.java)
    }
}
