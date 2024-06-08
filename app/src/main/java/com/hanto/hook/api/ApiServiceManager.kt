package com.hanto.hook.api

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response

class ApiServiceManager {
    private val apiService: ApiService = RetroServer.getInstance().create(ApiService::class.java)

    //============== User ==============// 카카오 로그인 제외 총 2개
    suspend fun managerGetMyInfo(): ApiResponse {
        return handleApiResponse { apiService.getMyInfo() }
    }

    suspend fun managerUpdateNickName(nickname: String): ApiResponse {
        val request = NicknameRequest(nickname)
        return handleApiResponse { apiService.updateNickName(request) }
    }

    //============== Hook ==============// get random hook 제외 총 5개

    suspend fun managerFindMyHooks(): ApiResponse {
        return handleApiResponse { apiService.findMyHooks() }
    }

    suspend fun managerFindMyHookByTag(tagID: Int): ApiResponse {
        return customHandleApiResponse { apiService.findMyHookByTag(tagID) }
    }

    suspend fun managerFindHookById(id: Int): ApiResponse {
        return handleApiResponse { apiService.findHookById(id) }
    }

    suspend fun managerCreateHook(title: String, description: String, url: String, tag: ArrayList<String>): ApiResponse {
        val request = HookRequest(title, description, url, tag)
        return handleApiResponse { apiService.createHook(request) }
    }

    suspend fun managerUpdateHook(id: Int, title: String, description: String, url: String, tag: ArrayList<String>): ApiResponse {
        val request = HookRequest(title, description, url, tag)
        return handleApiResponse { apiService.updateHook(id, request) }
    }
    suspend fun managerDeleteHook(id: Int): ApiResponse {
        return handleApiResponse { apiService.deleteHook(id) }
    }

    //============== TAG ==============// 랜덤 제외 현재 총 5개

    suspend fun managerFindMyTags(): ApiResponse {
        return handleApiResponse { apiService.findMyTags() }
    }

    suspend fun managerGetTagByName(name:String): ApiResponse {
        return handleApiResponse { apiService.getTagByName(name) }
    }

    suspend fun managerCreateTag(name: String): ApiResponse {
        val request = TagRequest(name)
        return handleApiResponse { apiService.createTag(request) }
    }

//    suspend fun managerUpdateTagName(id: Int, name: String): ApiResponse {
//        return handleApiResponse { apiService.updateTagName(id, name) }
//    }
    suspend fun managerUpdateTagName(id: Int, name: String): ApiResponse {
        val requestBody = mapOf("name" to name)
        return handleApiResponse { apiService.updateTagName(id, requestBody) }
    }

    suspend fun managerDeleteTag(id:Int): ApiResponse {
        return handleApiResponse { apiService.deleteTag(id) }
    }

    // 핸들러 ==============================================================================

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

    private suspend fun customHandleApiResponse(apiCall: suspend () -> Response<ApiResponse>): ApiResponse {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                // Parse the successful response
                response.body()?.let {
                    Log.d("ApiServiceManager", "성공 -- $it")
                    it as SelectedTagAndHookResponse
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