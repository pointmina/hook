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

/* 34~40행: Find My Hook: Filtered by Tag Id 에서는 Tag가 배열이 아니라 단일 object라서 파싱 오류가 남! 유니크한 차이라서 리스폰스 새로 작성*/
data class SelectedTagAndHookResponse (

    @SerializedName("count" ) var count : Int,
    @SerializedName("tag"   ) var tag   : Tag             = Tag(),
    @SerializedName("hooks" ) var hooks : ArrayList<Hook> = arrayListOf()

) : ApiResponse()

data class Result (

    @SerializedName("message" ) var message : String? = null

)