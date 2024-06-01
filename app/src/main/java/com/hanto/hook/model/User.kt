package com.hanto.hook.model

import com.google.gson.annotations.SerializedName

data class User (

    @SerializedName("id"                ) var id                : Int?    = null,
    @SerializedName("createdAt"         ) var createdAt         : String? = null,
    @SerializedName("updatedAt"         ) var updatedAt         : String? = null,
    @SerializedName("deletedAt"         ) var deletedAt         : String? = null,
    @SerializedName("nickname"          ) var nickname          : String? = null,
    @SerializedName("email"             ) var email             : String? = null,
    @SerializedName("kakaoId"           ) var kakaoId           : String? = null,
    @SerializedName("kakaoFullName"     ) var kakaoFullName     : String? = null,
    @SerializedName("kakaoRefreshToken" ) var kakaoRefreshToken : String? = null

)
