package com.hanto.hook.viewmodel

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
            try {
                val response = apiServiceManager.deleteMyHook(id)
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







