package com.hanto.hook.api

import com.google.gson.annotations.SerializedName

data class ApiRequest (

    @SerializedName("title"       ) var title       : String?           = null,
    @SerializedName("description" ) var description : String?           = null,
    @SerializedName("url"         ) var url         : String?           = null,
    @SerializedName("tags"        ) var tags        : ArrayList<String> = arrayListOf()

)