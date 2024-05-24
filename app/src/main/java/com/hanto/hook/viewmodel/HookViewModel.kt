package com.hanto.hook.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.api.ApiResponse
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import kotlinx.coroutines.launch

class HookViewModel(private val apiServiceManager: ApiServiceManager) : ViewModel() {
    private val _naviagteToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean>
        get() = _naviagteToHome
    private val _successData = MutableLiveData<SuccessResponse?>()
    private val _errorData = MutableLiveData<ErrorResponse?>()
    private val _tagDisplayNames = MutableLiveData<List<String>?>()
    private val _tagCreateSuccess = MutableLiveData<SuccessResponse?>()
    private val _tagCreateFail = MutableLiveData<ErrorResponse?>()
    val tagCreateSuccess: LiveData<SuccessResponse?>
        get() = _tagCreateSuccess
    val tagCreateFail: LiveData<ErrorResponse?>
        get() = _tagCreateFail

    val tagDisplayNames: LiveData<List<String>?>
        get() = _tagDisplayNames
    val successData: LiveData<SuccessResponse?>
        get() = _successData
    val errorData: LiveData<ErrorResponse?>
        get() = _errorData

    fun loadUserLogin() {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerUserLogin()
            }.onSuccess { response ->
                when (response) {
                    is SuccessResponse -> {
                        val accessToken = response.accessToken
                        val refreshToken = response.refreshToken
                        /*saveToken(accessToken, refreshToken)*/
                        _naviagteToHome.value = true
                        // API 호출 성공 -> 토큰 저장, 메인 페이지로 이동
                    }
                    is ErrorResponse -> {
                        Log.e("LoginError", "Login failed: ${response.message}")
                        // API 호출 실패
                    }
                }
            }.onFailure { exception ->
                Log.e("LoginError", "Login failed: ${exception.message}")
                // API 호출 실패
            }
        }
    }
    /*private fun saveToken(accessToken: String, refreshToken: String) {
        val sharedPref = application.getSharedPreferences("whdgkqtjfrPrpdlxmdnpdl", Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            putString("ACCESS_TOKEN", accessToken)
            putString("REFRESH_TOKEN", refreshToken)
            apply()
        }
    }*/

    fun loadCreateMyTag(name: String) {
        viewModelScope.launch {
            try {
                val result = apiServiceManager.createMyTag(name)
                if (result is SuccessResponse) {
                    _tagCreateSuccess.postValue(result)
                } else if (result is ErrorResponse) {
                    _tagCreateFail.postValue(result)
                }
            } catch (e: Exception) {
                val errorResponse = ErrorResponse(message = e.message)
                _tagCreateFail.postValue(errorResponse)
            }
        }
    }





    fun loadDeleteMyHook(id: Int) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.deleteMyHook(id)
            }.onSuccess { response ->
                when (response) {
                    is SuccessResponse -> {
                        _successData.value = response
                    }
                    is ErrorResponse -> {
                        _errorData.value = response
                    }
                }
            }.onFailure { exception ->
                Log.e("Delete Hook Error", "${exception.message}")
            }
        }
    }

    fun loadCreateMyHook(
        title: String,
        description: String,
        url: String,
        tag: ArrayList<String>
    ) {
        viewModelScope.launch {
            try {
                val response =
                    apiServiceManager.postCreateHook(title, description, url, tag)
                when (response) {
                    is SuccessResponse -> _successData.postValue(response)
                    is ErrorResponse -> _errorData.postValue(response)
                    else -> {
                        // 예외 처리
                    }
                }
            } catch (e: Exception) {
                Log.d("HookViewModel", "기타 오류: ${e.message}")
                _errorData.value = ErrorResponse(error = "네트워크 오류: ${e.message}")
            }
        }
    }

    fun loadFindMyHooks() {
        viewModelScope.launch {
            try {
                val response = apiServiceManager.getFindMyHooks()
                when (response) {
                    is SuccessResponse -> {
                        _successData.value = response
                    }

                    is ErrorResponse -> {
                        _errorData.value = response
                    }

                    else -> {

                    }
                }
            } catch (e: Exception) {
                Log.d("HookViewModel", "기타 오류: ${e.message}")
                _errorData.value = ErrorResponse(error = "네트워크 오류: ${e.message}")
            }
        }
    }

    fun loadFindMyTags() {
        viewModelScope.launch {
            try {
                val response = apiServiceManager.getFindMyTags()
                when (response) {
                    is SuccessResponse -> {
                        _successData.value = response
                    }

                    is ErrorResponse -> {
                        _errorData.value = response
                    }

                    else -> {

                    }
                }
            } catch (e: Exception) {
                Log.d("HookViewModel", "기타 오류: ${e.message}")
                _errorData.value = ErrorResponse(error = "네트워크 오류: ${e.message}")
            }
        }
    }

    fun loadFindMyDisplayName() {
        viewModelScope.launch {
            try {
                val response = apiServiceManager.getFindMyTags()
                when (response) {
                    is SuccessResponse -> {
                        _tagDisplayNames.value = response.tag.mapNotNull { it.displayName }
                    }

                    is ErrorResponse -> {
                        // Handle error response
                    }

                    else -> {

                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }

    fun loadMyInfo() {
        viewModelScope.launch {
            try {
                val response = apiServiceManager.getMyInfo()
                when (response) {
                    is SuccessResponse -> {
                        _successData.value = response
                    }

                    is ErrorResponse -> {
                        _errorData.value = response
                    }

                    else -> {

                    }
                }
            } catch (e: Exception) {
                Log.d("HookViewModel", "기타 오류: ${e.message}")
                _errorData.value = ErrorResponse(error = "네트워크 오류: ${e.message}")
            }
        }
    }
}







