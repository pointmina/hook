package com.hanto.hook.api

import com.google.gson.annotations.SerializedName

data class TagRequest(
    @SerializedName("name") val name: String
)

