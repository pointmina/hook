package com.hanto.hook.api

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: BuildConfig로 BASE_URL 노출되지 않게 숨기기, Bearer 토큰 유저마다 다르게 설정
object RetroServer {
    val BASE_URL =  "http://ec2-43-201-63-64.ap-northeast-2.compute.amazonaws.com:3030"

    private val okHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        val authInterceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIzIiwiaWF0IjoxNzE1Nzc1NDkxfQ.oLYke0tJeJVnEgoLl2xEr19fvKwJRxVL-WyBBE4R54M")
                .build()
            chain.proceed(newRequest)
        }

        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    private val gson = GsonBuilder()
        .registerTypeAdapter(ApiResponse::class.java, ApiResponseAdapter())
        .create()

    private val retrofitClient: Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getInstance(): Retrofit {
        return retrofitClient
    }
}