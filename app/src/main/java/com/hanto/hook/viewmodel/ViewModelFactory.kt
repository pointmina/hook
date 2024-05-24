package com.hanto.hook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hanto.hook.api.ApiServiceManager

class ViewModelFactory(private val apiServiceManager: ApiServiceManager): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(apiServiceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
