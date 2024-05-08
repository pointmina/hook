package com.hanto.hook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.api.ApiServiceManager

// 기본 값으로 잘 돌아가는 클래스!! 수정 금지
class ViewModelFactory(private val apiServiceManager: ApiServiceManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HookViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HookViewModel(apiServiceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
