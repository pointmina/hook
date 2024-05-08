package com.hanto.hook.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SuccessResponse
import kotlinx.coroutines.launch

class HookViewModel(private val apiServiceManager: ApiServiceManager) : ViewModel() {
    private val _successData = MutableLiveData<SuccessResponse?>()
    private val _errorData = MutableLiveData<ErrorResponse?>()

    val successData: LiveData<SuccessResponse?>
        get() = _successData
    val errorData: LiveData<ErrorResponse?>
        get() = _errorData






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
                }
            } catch (e: Exception) {
                Log.d("HookViewModel", "기타 오류: ${e.message}")
                _errorData.value = ErrorResponse(error = "네트워크 오류: ${e.message}")
            }
        }
    }
}
