package com.hanto.Hook.repository

import com.hanto.Hook.api.ApiRequestData
import com.hanto.Hook.api.RetroServer

class Repository {

    private val client = RetroServer.getInstance().create(ApiRequestData::class.java)

    suspend fun getAllData() = client.getAllHook()
}
