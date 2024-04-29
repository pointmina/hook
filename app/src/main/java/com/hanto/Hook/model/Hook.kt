package com.hanto.Hook.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Hook(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("createdAt")
    val createdAt: Date?,
    @SerializedName("updatedAt")
    val updatedAt: Date?,
    @SerializedName("deletedAt")
    val deletedAt: Date?,
    @SerializedName("userId")
    val userId: Int?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("url")
    val url: String?,
    //List -> Array로 바꿨음
    @SerializedName("tag")
    val tag: Array<String>?
)
// 여기서 itemlist에 띄울거는 title, description, url, tag 네 가지
