package com.hanto.hook.api

import com.google.gson.annotations.SerializedName


open class ApiRequest()

data class HookRequest(
    @SerializedName("title") var title: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("tags") var tags: ArrayList<String>?,
    @SerializedName("suggestTags") var suggestTags: Boolean = true
    ): ApiRequest()

data class TagRequest (

    @SerializedName("name") val name: String

): ApiRequest()

data class NicknameRequest (

    @SerializedName("nickname"        ) var nickname        : String?           = null,

): ApiRequest()