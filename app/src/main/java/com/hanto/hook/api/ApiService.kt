package com.hanto.hook.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {
    //============== User ==============
    @GET("/api/user")
    suspend fun getMyInfo(): Response<ApiResponse>

    @PUT("/api/user")
    suspend fun updateNickName(@Body request: NicknameRequest): Response<ApiResponse>

    //============== Hook ==============
    @GET("/api/hook")
    suspend fun findMyHooks(): Response<ApiResponse>

    @GET("/api/hook")
    suspend fun findMyHookByTag(@Query("tag") tag: Int): Response<ApiResponse>

    @GET("/api/hook/{id}")
    suspend fun findHookById(@Path("id") id: Int): Response<ApiResponse>

    @POST("/api/hook")
    suspend fun createHook(@Body request: HookRequest): Response<ApiResponse>

    @PUT("/api/hook/{id}")
    suspend fun updateHook(@Path("id") id: Int, @Body request: HookRequest) : Response<ApiResponse>

    @DELETE("/api/hook/{id}")
    suspend fun deleteHook(@Path("id") id: Int) : Response<ApiResponse>

    //============== TAG ==============
    @GET("/api/tag/all")
    suspend fun findMyTags() : Response<ApiResponse>

    @GET("/api/tag")
    suspend fun getTagByName(@Query("name") name: String): Response<ApiResponse>

    @POST("/api/tag")
    suspend fun createTag(@Body request: TagRequest): Response<ApiResponse>

    @PUT("/api/tag/{id}")
//    suspend fun updateTagName(@Path("id") id: Int, @Body name: String) : Response<ApiResponse>
    suspend fun updateTagName(@Path("id") id: Int, @Body body: Map<String, String>): Response<ApiResponse>

    @DELETE("/api/tag/{id}")
    suspend fun deleteTag(@Path("id") id: Int) : Response<ApiResponse>
}