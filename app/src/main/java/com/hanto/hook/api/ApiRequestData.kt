package com.hanto.hook.api

import com.hanto.hook.model.Hook
import com.hanto.hook.model.Tag
import retrofit2.http.GET

//http 통신을 위한 인터페이스
interface ApiRequestData {
    @GET("pointmina/Hook/master/dummy.json")
    suspend fun getAllHook(): List<Hook>

    @GET("pointmina/Hook/master/dummyTag.json")
    suspend fun  getAllTag(): List<Tag>
}
