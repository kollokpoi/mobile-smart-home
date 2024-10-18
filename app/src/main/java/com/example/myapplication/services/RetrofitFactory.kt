package com.example.myapplication.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
    private const val BASE_URL = "http://89.179.74.7:5000/"
    private val okHttpClient = OkHttpClient.Builder()
        .readTimeout(180, TimeUnit.SECONDS)
        .build()

    val instance: IApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(IApi::class.java)
    }


    val instanceWithoutTimeOut: IApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(IApi::class.java)
    }
}