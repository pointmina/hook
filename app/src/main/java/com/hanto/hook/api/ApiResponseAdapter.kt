package com.hanto.hook.api

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ApiResponseAdapter : TypeAdapter<ApiResponse>() {
    override fun write(out: JsonWriter?, value: ApiResponse?) {
        throw UnsupportedOperationException("ApiResponseAdapter is only used for deserialization")
    }

    override fun read(reader: JsonReader?): ApiResponse {
        val gson = Gson()
        val jsonObject = JsonParser.parseReader(reader).asJsonObject

        return if (jsonObject.has("statusCode")) {
            // 에러 응답으로 간주
            gson.fromJson(jsonObject, ErrorResponse::class.java)
        } else {
            // 성공 응답으로 간주
            gson.fromJson(jsonObject, SuccessResponse::class.java)
        }
    }
}