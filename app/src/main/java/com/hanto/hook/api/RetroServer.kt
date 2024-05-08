package com.hanto.hook.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hanto.hook.api.RetroServer.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: BuildConfig로 베이스 서버 URL 숨겨주세요. (사용할 때 import ...BuildConfig.BASE_URL 필요)
object RetroServer {
    val BASE_URL = "https://821a656d-8d6c-4d9b-b2a5-33546fe80917.mock.pstmn.io"
//    val BASE_URL = "https://910211dc-c23e-4c24-b62d-3a62fbc99739.mock.pstmn.io/"
    // 오후 10:18 2024-05-06 : API 컬렉션 변경됨, 새로 목업 서버 빌드

    /*val responseInterceptor = Interceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        val modifiedResponse = when (originalResponse.code) {
            in 200..299 -> originalResponse.newBuilder()
                .header("ResponseType", "Success")
                .build()
            else -> originalResponse.newBuilder()
                .header("ResponseType", "Error")
                .build()
        }
        modifiedResponse
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(responseInterceptor)
        .build()

    private val gson = GsonBuilder()
        .registerTypeAdapter(ApiResponse::class.java, ApiResponseDeserializer())
        .create()*/
    private val gson = GsonBuilder()
        .registerTypeAdapter(ApiResponse::class.java, ApiResponseAdapter())
        .create()

    private val retrofitClient: Retrofit = Retrofit.Builder()
        //.client(okHttpClient) 지금은 안 쓸 건데, 나중에 유저 api 활성화되면 헤드에 토큰 정보 넣어서 보낼 것 같음.
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // 클라이언트 인스턴스 생성 함수
    fun getInstance(): Retrofit {
        return retrofitClient
    }
}