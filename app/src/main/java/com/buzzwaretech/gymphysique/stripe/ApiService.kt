package com.buzzwaretech.gymphysique.stripe

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Accept: application/json")
    @POST("/bandy/api/tailgategetEphemeralKey")
    fun getEphemeralKey(@Body body: RequestBody): Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("/bandy/api/tailgatefirststep")
    fun createClient(@Body requestBody: RequestBody): Call<CustomerResponse>

    @Headers("Accept: application/json")
    @POST("/bandy/api/tailgateaccoutpaymentnew")
    fun stripePaymentIntent(@Body requestBody: RequestBody): Call<AccountPaymentReturnData>

}