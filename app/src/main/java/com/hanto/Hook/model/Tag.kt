package com.hanto.Hook.model

import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("tagName")
    val uniqueTag: String?,
    @SerializedName("hook")
    val tagByHook: List<String>?,
    val initalValue: Boolean = false // 모든 태그를 false로 설정
)
