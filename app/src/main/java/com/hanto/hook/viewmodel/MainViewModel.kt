package com.hanto.hook.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.hook.api.ApiResponse
import com.hanto.hook.api.ApiServiceManager
import com.hanto.hook.api.ErrorResponse
import com.hanto.hook.api.SelectedTagAndHookResponse
import com.hanto.hook.api.SuccessResponse
import kotlinx.coroutines.launch

class MainViewModel(private val apiServiceManager: ApiServiceManager) : ViewModel() {
    private val _successData = MutableLiveData<SuccessResponse?>()
    val successData: LiveData<SuccessResponse?>
        get() = _successData

    private val _errorData = MutableLiveData<ErrorResponse?>()
    val errorData: LiveData<ErrorResponse?>
        get() = _errorData

    // 유저 2개 ===========================================================================
    private val _userData = MutableLiveData<SuccessResponse?>()
    val userData: LiveData<SuccessResponse?>
        get() = _userData

    fun loadGetMyInfo() {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerGetMyInfo()
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _userData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                        Log.e("Error - loadGetMyInfo", "$errorData")
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadGetMyInfo", "${exception.message}")
            }
        }
    }

    fun loadUpdateNickName(nickname: String) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerUpdateNickName(nickname)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadUpdateNickName", "${exception.message}")
            }
        }
    }
    // 훅 5개 ==============================================================================
    private val _hookData = MutableLiveData<SuccessResponse?>()
    val hookData: LiveData<SuccessResponse?>
        get() = _hookData
    fun loadFindMyHooks() {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerFindMyHooks()
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _hookData.value = result
                        Log.d("MainViewModel","$hookData")
                    }
                    is ErrorResponse -> {
                        _errorData.value = result
                        Log.e("Error - loadFindMyHooks", "$errorData")
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadFindMyHooks", "${exception.message}")
            }
        }
    }


    private val _tagFilteredHooks = MutableLiveData<SelectedTagAndHookResponse?>()
    val tagFilteredHooks: LiveData<SelectedTagAndHookResponse?>
        get() = _tagFilteredHooks

    fun loadFindMyHookByTag(tagID: Int) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerFindMyHookByTag(tagID)
            }.onSuccess { result ->
                when (result) {
                    is SelectedTagAndHookResponse -> {
                        _tagFilteredHooks.value = result
                        Log.d("MainViewModel","$_tagFilteredHooks")
                    }
                    is ErrorResponse -> {
                        _errorData.value = result
                        Log.e("Error - loadFindMyHookByTag", "$errorData")
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadFindMyHookByTag", "${exception.message}")
            }
        }
    }

    fun loadFindHookById(id: Int) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerFindHookById(id)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _hookData.value = result
                    }
                    is ErrorResponse -> {
                        _errorData.value = result
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadFindHookById", "${exception.message}")
            }
        }
    }


    fun loadCreateHook(title: String, description: String, url: String, tag: ArrayList<String>) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerCreateHook(title,description,url,tag)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadCreateHook", "${exception.message}")
            }
        }
    }

    fun loadUpdateHook(id:Int, title: String, description: String, url: String, tag: ArrayList<String>?) {
        viewModelScope.launch {
            runCatching {
                tag?.let { apiServiceManager.managerUpdateHook(id, title, description, url, it) }
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadUpdateHook", "${exception.message}")
            }
        }
    }

    fun loadDeleteHook(id: Int) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerDeleteHook(id)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadDeleteHook", "${exception.message}")
            }
        }
    }

    // 태그 5개 ==========================================================================
    private val _tagData = MutableLiveData<SuccessResponse?>()
    val tagData : LiveData<SuccessResponse?>

        get() = _tagData
    private val _tagDisplayNames = MutableLiveData<List<String?>>()
    val tagDisplayNames : LiveData<List<String?>>

        get() = _tagDisplayNames
    fun loadFindMyTags() {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerFindMyTags()
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _tagData.postValue(result)
                        _tagDisplayNames.postValue(result.tag.mapNotNull { it.displayName })
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadFindMyTags", "${exception.message}")
            }
        }
    }

    fun loadGetTagByName(name: String) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerGetTagByName(name)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadGetTagByName", "${exception.message}")
            }
        }
    }

    fun loadCreateTag(name: String) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerCreateTag(name)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - Create Tag", "${exception.message}")
            }
        }
    }

    fun loadUpdateTag(id: Int, name: String) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerUpdateTagName(id, name)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadUpdateTag", "${exception.message}")
            }
        }
    }

    fun loadDeleteTag(id: Int) {
        viewModelScope.launch {
            runCatching {
                apiServiceManager.managerDeleteTag(id)
            }.onSuccess { result ->
                when (result) {
                    is SuccessResponse -> {
                        _successData.postValue(result)
                    }
                    is ErrorResponse -> {
                        _errorData.postValue(result)
                    }
                }
            }.onFailure { exception ->
                Log.e("Fail - loadDeleteTag", "${exception.message}")
            }
        }
    }
}