package com.example.ticketmasterapp.network

import com.example.ticketmasterapp.models.AccessTokenResponse
import com.example.ticketmasterapp.models.StkPushRequest
import com.example.ticketmasterapp.models.StkPushResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaApi {

    @GET("oauth/v1/generate?grant_type=client_credentials")
    fun getAccessToken(
        @Header("Authorization") auth: String
    ): Call<AccessTokenResponse>

    @POST("mpesa/stkpush/v1/processrequest")
    fun stkPush(
        @Header("Authorization") token: String,
        @Body stkPushRequest: StkPushRequest
    ): Call<StkPushResponse>
}
