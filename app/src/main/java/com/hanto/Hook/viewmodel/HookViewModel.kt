package com.hanto.apitest

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.Hook.model.Hook
import com.hanto.Hook.repository.Repository
import kotlinx.coroutines.launch

class HookViewModel : ViewModel() {

    private val repository = Repository()

    private val _result = MutableLiveData<List<Hook>>()
    val result: LiveData<List<Hook>>
        get() = _result

    fun getAllData() = viewModelScope.launch {
        Log.d("HookViewModel", repository.getAllData().toString())
        _result.value = repository.getAllData()
    }
}