package com.hanto.hook.model

import com.google.gson.annotations.SerializedName

data class Hook (
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("createdAt"   ) var createdAt   : String? = null,
    @SerializedName("updatedAt"   ) var updatedAt   : String? = null,
    @SerializedName("deletedAt"   ) var deletedAt   : String? = null,
    @SerializedName("userId"      ) var userId      : Int?    = null,
    @SerializedName("title"       ) var title       : String? = null,
    @SerializedName("description" ) var description : String? = null,
    @SerializedName("url"         ) var url         : String? = null,
    @SerializedName("tags"        ) var tags        : List<Tags>? = null
)

data class Tags (
    @SerializedName("id"          ) var id          : Int?    = null,
    @SerializedName("createdAt"   ) var createdAt   : String? = null,
    @SerializedName("userId"      ) var userId      : Int?    = null,
    @SerializedName("displayName" ) var displayName : String? = null
)
