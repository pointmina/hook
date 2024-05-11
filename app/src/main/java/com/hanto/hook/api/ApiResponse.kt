package com.hanto.hook.api

import com.google.gson.annotations.SerializedName
import com.hanto.hook.model.Hook
import com.hanto.hook.model.Tag

sealed class ApiResponse

data class SuccessResponse(
    @SerializedName("count") var count: Int,
    @SerializedName("tag") var tag: List<Tag>,
    @SerializedName("hooks") var hooks: ArrayList<Hook> = arrayListOf()
) : ApiResponse()

data class ErrorResponse(
    @SerializedName("statusCode") var statusCode: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("error") var error: String? = null
) : ApiResponse()

