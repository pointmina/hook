package com.hanto.hook.api

import android.util.Log
import com.google.gson.Gson
import com.hanto.hook.api.ApiService
import com.hanto.hook.api.RetroServer
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import com.hanto.hook.api.ApiServiceManager

//Api 호출을 관리하기 위한 클래스 -> ApiServiceManager
//Retrofit을 사용하여 서버에 API요청을 보낸다.

class ApiServiceManager {
    //ApiService 인터페이스의 인스턴스를 가짐 -> Retrofit을 사용하여 실제 네트워크 호출을 처리한다.
    private val apiService: ApiService = RetroServer.getInstance().create(ApiService::class.java)


    //서버에서 findMyHooks API를 호출하고 결과를 처리한다. 비동기 호출 -> suspend
    suspend fun getFindMyHooks(): ApiResponse {
        return try {
            val response = apiService.findMyHooks() // Retrofit service call
            if (response.isSuccessful) {
                // Parse the successful response
                response.body()?.let {
                    Log.d("ApiServiceManager", "성공 -- $it")
                    it as SuccessResponse
                } ?: ErrorResponse()
            } else {
                // Parse the error response
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
                    .also {
                        Log.d("ApiServiceManager", "에러 -- ${it.error}")
                    }
            }
        } catch (e: Exception) {
            Log.d("ApiServiceManager", "예상치 못한 오류", e)
            ErrorResponse(error = "예상치 못한 오류 발생: ${e.message}")
        }
    }
}
