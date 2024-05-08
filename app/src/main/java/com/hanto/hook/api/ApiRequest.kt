package com.hanto.hook.api

data class ApiRequest (
    val title: String,
    val description: String,
    val url: String
)

data class HookRequest (
    val title: String,
    val description: String,
    val url: String
)