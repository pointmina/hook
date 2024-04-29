package com.hanto.Hook.api

import com.hanto.Hook.model.Hook
import retrofit2.http.GET

//http 통신을 위한 인터페이스
interface ApiRequestData {
    @GET("pointmina/Hook/master/dummy.json")
    suspend fun getAllHook(): List<Hook>
}
