package com.hanto.hook.api

import com.hanto.hook.api.ApiResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// 서버의 어떤 api랑 통신해서 어떤 응답을 어떤 식으로 가져올 건지 대해서 청사진을 그림
interface ApiService {
    @GET("/api/auth/kakao/signin") // 로그인
    suspend fun userLogin(): Response<ApiResponse>
    @GET("/api/hook")
    suspend fun findMyHooks(): Response<ApiResponse>
    // 200: ok, 200: none.
    @POST("/api/hook")
    suspend fun createHook(@Body requestData: ApiRequest): Response<ApiResponse>
    // 201: created, 400: Bad Request
    @PUT("/api/hook/12")
    suspend fun updateHook() : Response<ApiResponse>
    // 200: ok, 400: Bad Request, 403: Forbidden, 404: Not Found by ID
    @DELETE("/api/hook/{id}")
    suspend fun deleteHook(@Path("id") id: Int) : Response<ApiResponse>
    // 200: ok, 404: Not Found by ID

    //============== TAG ==============//
    @GET("/api/tag/all")
    suspend fun findMyTags() : Response<ApiResponse>

    @GET("/api/tag/all")
    suspend fun tagByName(@Query("name") name: String): Response<ApiResponse>

    @POST("/api/tag/")
    suspend fun createTag(@Body request: TagRequest): Response<ApiResponse>

    @PUT("/api/tag/{id}")
    suspend fun updateTagName(@Path("id") id: Int) : Response<ApiResponse> // tag id 주소에 넣고 name 주면 됨

    @DELETE("/api/tag/4")
    suspend fun deleteTag() : ApiResponse

    // ============== USER ==============//
    @GET("/api/user")
    suspend fun myInfo(): Response<ApiResponse>

    //아래 함수는 나중에 DI에 쓰려고 만들어둔 것. 지금은 쓸 일 없음.
    fun provideApiService(retrofit: Retrofit): ApiService{
        return retrofit.create(ApiService::class.java)
    }
}
