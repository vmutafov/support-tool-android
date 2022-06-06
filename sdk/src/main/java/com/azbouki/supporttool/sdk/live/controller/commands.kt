package com.azbouki.supporttool.sdk.live.controller

import com.google.gson.*
import io.sentry.Breadcrumb

typealias Type = String
const val MetricsType: Type = "metrics"
const val CodeSnippetType: Type = "code"

sealed class Request(open val requestId: Int)
sealed class Response(open val requestId: Int, val type: Type)

data class MetricsRequest(
    override val requestId: Int
) : Request(requestId)

data class CodeSnippetRequest(
    override val requestId: Int,
    val code: String
) : Request(requestId)

data class MetricsResponse(
    override val requestId: Int,
    val data: Any
) : Response(requestId, MetricsType)

data class CodeSnippetResponse(
    override val requestId: Int,
    val hasError: Boolean,
    val result: String
) : Response(requestId, CodeSnippetType)


class EventData(val breadcrumb: Breadcrumb) {
    val type = "event"
}

class RequestDeserializer : JsonDeserializer<Request?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: java.lang.reflect.Type?,
        context: JsonDeserializationContext?
    ): Request? {
        val jsonObject: JsonObject = json.asJsonObject
        val type = jsonObject.get("type").asString
        val requestId = jsonObject.get("requestId").asInt

        return when (type) {
            MetricsType -> MetricsRequest(requestId)
            CodeSnippetType -> {
                val code = jsonObject.get("code").asString
                CodeSnippetRequest(requestId, code)
            }
            else -> null
        }
    }
}