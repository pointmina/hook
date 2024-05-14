package com.hanto.hook.urlHandler
import com.google.gson.Gson

class DataToJsonConverter {
    fun convertToJson(title: String, description: String, url: String, tag: String?): String {
        // 데이터를 JSON 형식으로 변환
        val jsonData = mapOf(
            "title" to title,
            "description" to description,
            "url" to url,
            "tag" to tag
        )

        // Gson을 사용하여 JSON 문자열로 변환
        val gson = Gson()
        return gson.toJson(jsonData)
    }
}