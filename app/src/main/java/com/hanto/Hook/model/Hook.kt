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
    @SerializedName("tag")
    val tag: List<String>?
)


