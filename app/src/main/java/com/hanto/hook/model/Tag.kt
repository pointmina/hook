package com.hanto.hook.model

import com.google.gson.annotations.SerializedName

data class Tag (

    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("createdAt"   ) var createdAt   : String? = null,
    @SerializedName("userId"      ) var userId      : Int?    = null,
    @SerializedName("displayName" ) var displayName : String? = null

)