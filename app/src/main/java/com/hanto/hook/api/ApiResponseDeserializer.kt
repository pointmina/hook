package com.hanto.hook.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


// Result가 json object가 아니라 string 으로 파싱되는 것 (nickname -- 수정완료 result 때문) 해결하려고 만들어놨는데 그냥 안 쓸듯?
class ApiResponseDeserializer : JsonDeserializer<Result?> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Result? {
        return if (json.isJsonObject) {
            context.deserialize(json, Result::class.java)
        } else if (json.isJsonPrimitive) {
            Result(json.asString)
        } else {
            null
        }
    }
}