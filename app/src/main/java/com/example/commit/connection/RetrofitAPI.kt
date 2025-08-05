package com.example.commit.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import com.example.commit.data.model.CommissionDetailResponse

interface RetrofitAPI {

    // 회원가입
    @POST("/api/users")
    fun signUp(
        @Body request: RetrofitClient.RequestSignUp
    ): Call<RetrofitClient.ResponseSignUp>

    //커미션 상세보기 (postScreen)
        @GET("/api/commissions/{commissionId}")
        suspend fun getCommissionDetail(
            @Path("commissionId") commissionId: Int
        ): CommissionDetailResponse
}
