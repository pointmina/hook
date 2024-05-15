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
    val BASE_URL =  "https://7171f183-32c7-4634-8d63-dd909edac6c1.mock.pstmn.io"
        //우리 실제 서버 (포트 번호 3030번)
        // "http://ec2-43-201-63-64.ap-northeast-2.compute.amazonaws.com:3030"

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
    .addInterceptor() //responseInterceptor
    .build()
*/
val okHttpClient = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI0IiwiaWF0IjoxNzE0ODkyOTg0fQ.tO4GskZ_Y4yU7xBG8bZRId-0t_GTXmCd6blFskPOIuY")
            .method(original.method, original.body)
        val request = requestBuilder.build()
        chain.proceed(request)
    }
    .build()
private val gson = GsonBuilder()
    .registerTypeAdapter(ApiResponse::class.java, ApiResponseAdapter())
    .create()

private val retrofitClient: Retrofit = Retrofit.Builder()
    .client(okHttpClient) //지금은 안 쓸 건데, 나중에 유저 api 활성화되면 헤드에 토큰 정보 넣어서 보낼 것 같음.
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()

// 클라이언트 인스턴스 생성 함수
fun getInstance(): Retrofit {
    return retrofitClient
}
}