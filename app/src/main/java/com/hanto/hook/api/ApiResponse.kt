package com.hanto.hook.api

import com.google.gson.annotations.SerializedName
import com.hanto.hook.model.Hook
import com.hanto.hook.model.Tag
import com.hanto.hook.model.User

open class ApiResponse()

data class SuccessResponse(

    @SerializedName("count") var count: Int,
    @SerializedName("tag") var tag: List<Tag>,
    @SerializedName("hooks") var hooks: ArrayList<Hook> = arrayListOf(),

    @SerializedName("result" ) var result : Result? = Result(),

    @SerializedName("accessToken"  ) var accessToken  : String,
    @SerializedName("refreshToken" ) var refreshToken : String,

    @SerializedName("user" ) var user: User

) : ApiResponse()

data class ErrorResponse(

    @SerializedName("statusCode") var statusCode: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("error") var error: String? = null

) : ApiResponse()

data class Result (

    @SerializedName("message" ) var message : String? = null

)