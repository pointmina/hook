package com.hanto.hook.api

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.google.gson.JsonParser

class ApiResponseAdapter : TypeAdapter<ApiResponse>() {
    override fun write(out: JsonWriter?, value: ApiResponse?) {
        throw UnsupportedOperationException("ApiResponseAdapter is only used for deserialization")
    }

    override fun read(reader: JsonReader?): ApiResponse {
        val gson = Gson()
        val jsonObject = JsonParser.parseReader(reader).asJsonObject

        return if (jsonObject.has("statusCode")) {
            gson.fromJson(jsonObject, ErrorResponse::class.java)
        } else if (jsonObject.has("hooks") && !jsonObject.get("tag").isJsonNull) {
            gson.fromJson(jsonObject, SelectedTagAndHookResponse::class.java)
        } else {
            gson.fromJson(jsonObject, SuccessResponse::class.java)
        }
    }
}