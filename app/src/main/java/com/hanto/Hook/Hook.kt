package com.hanto.Hook

import androidx.annotation.DrawableRes



data class Hook(
    @DrawableRes val folderResourceId: Int? = null,
    val urlTitle: String = "",
    val urlLink: String = "",
    val urlDescription: String? = "",
    val tags: List<String> = emptyList()

)
