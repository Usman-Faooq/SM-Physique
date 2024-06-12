package com.buzzwaretech.gymphysique.stripe

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StripeController {
    //private const val BASE_URL = "https://us-central1-gim-africa.cloudfunctions.net/"
    private const val BASE_URL = "https://buzzwaretech.com"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}