package com.hanto.hook.api

import android.util.Log
import com.google.gson.Gson
import com.hanto.hook.api.ApiService
import com.hanto.hook.api.RetroServer
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import com.hanto.hook.api.ApiServiceManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body

class ApiServiceManager {
    private val apiService: ApiService = RetroServer.getInstance().create(ApiService::class.java)

    suspend fun managerUserLogin(): ApiResponse {
        return handleApiResponse { apiService.userLogin() }
    }

    suspend fun createMyTag(name: String): ApiResponse {
        val request = TagRequest(name)
        return handleApiResponse { apiService.createTag(request) }
    }

    /*suspend fun updateMyTagName(id: Int, name: String): ApiResponse {
        val request = name
        return handleApiResponse { apiService.updateTagName(id, request) }
    }*/

    suspend fun deleteMyHook(id: Int): ApiResponse {
        return handleApiResponse { apiService.deleteHook(id) }
    }
    suspend fun postCreateHook(title: String, description: String, url: String, tag: ArrayList<String>): ApiResponse {
        val request = ApiRequest(title, description, url, tag)
        return handleApiResponse { apiService.createHook(request) }
    }

    suspend fun getFindMyTags(): ApiResponse {
        return handleApiResponse { apiService.findMyTags() }
    }

    suspend fun getFindMyHooks(): ApiResponse {
        return handleApiResponse { apiService.findMyHooks() }
    }

    suspend fun getMyInfo(): ApiResponse {
        return handleApiResponse { apiService.myInfo() }
    }

    private suspend fun handleApiResponse(apiCall: suspend () -> Response<ApiResponse>): ApiResponse {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                // Parse the successful response
                response.body()?.let {
                    Log.d("ApiServiceManager", "성공 -- $it")
                    it as SuccessResponse
                } ?: ErrorResponse()
            } else {
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java).also {
                    Log.d("ApiServiceManager", "에러 -- ${it.error}")
                }
            }
        } catch (e: Exception) {
            Log.d("ApiServiceManager", "예상치 못한 오류", e)
            ErrorResponse(error = "예상치 못한 오류 발생: ${e.message}")
        }
    }
}
