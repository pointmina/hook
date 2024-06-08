package com.hanto.hook.api

import com.google.gson.annotations.SerializedName


open class ApiRequest()

data class HookRequest(

    @SerializedName("title"       ) var title: String?           = null,
    @SerializedName("description" ) var description: String?           = null,
    @SerializedName("url"         ) var url: String?           = null,
    @SerializedName("tags"        ) var tags: ArrayList<String>? = arrayListOf(),

    ): ApiRequest()

data class TagRequest (

    @SerializedName("name") val name: String

): ApiRequest()

data class NicknameRequest (

    @SerializedName("nickname"        ) var nickname        : String?           = null,

): ApiRequest()