package com.example.commit.fcm

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.example.commit.connection.RetrofitObject
import com.example.commit.connection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FcmMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d("FCMToken", "FCMToken = $token")

        val prefs = applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)

        val api = RetrofitObject.getRetrofitService(applicationContext)
        val body = RetrofitClient.FcmTokenRegisterRequest(fcmToken = token)
        api.registerFcmToken(body).enqueue(object : Callback<
                RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>
                > {
            override fun onResponse(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>,
                response: Response<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>
            ) {
                if (response.isSuccessful && response.body()?.success != null) {
                    prefs.edit().putString("fcmToken", token).apply()
                }
            }
            override fun onFailure(
                call: Call<RetrofitClient.ApiResponse<RetrofitClient.FcmTokenRegisterSuccess>>,
                t: Throwable
            ) { /* 로깅 */ }
        })
    }
}
