package com.example.commit.connection

import android.content.Context
import android.util.Log
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

    fun getRetrofitService(context: Context): RetrofitAPI {
        // 토큰 자동 추가 인터셉터
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            getAccessToken(context)?.let { token ->
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

/*object RetrofitObject {

    private const val BASE_URL = "https://commit.n-e.kr"

    // 하드코딩된 토큰
    private const val HARDCODED_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxIiwibmlja25hbWUiOiJ1c2VyX29uZSIsImFjY291bnRJZCI6IjEiLCJwcm92aWRlciI6Imtha2FvIiwicm9sZSI6ImNsaWVudCIsImlhdCI6MTc1NTA4OTM2OCwiZXhwIjoxNzU1MTc1NzY4fQ.Nu4QQEN47tPwKJLUdUYBrJ_Kzl2DzfcXArR6Lv7a1Zw"

    // context는 받지만, SharedPreferences 무시하고 무조건 HARDCODED_TOKEN 사용
    fun getRetrofitService(context: Context): RetrofitAPI {
        val authInterceptor = Interceptor { chain ->
            Log.d("RetrofitObject", "요청에 사용된 accessToken = $HARDCODED_TOKEN")

            val requestBuilder = chain.request().newBuilder()
            requestBuilder.addHeader("Authorization", HARDCODED_TOKEN)
            chain.proceed(requestBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(RetrofitAPI::class.java)
    }
}*/



