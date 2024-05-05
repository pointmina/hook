package com.hanto.hook.repository

import com.hanto.hook.api.ApiRequestData
import com.hanto.hook.api.RetroServer

class Repository {

    private val client = RetroServer.getInstance().create(ApiRequestData::class.java)

    suspend fun getAllData() = client.getAllHook()
    suspend fun getAllTag() = client.getAllTag()
}
