package com.xbot.data.models.dto

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("status")
@Serializable
internal sealed interface Response {
    @Serializable
    @SerialName("error")
    data class Error(
        @SerialName("code") val code: String,
        @SerialName("message") val message: String
    ) : Response

    @Serializable
    @SerialName("ok")
    data class Success(
        @SerialName("totalResults") val totalResults: Int,
        @SerialName("articles") val articles: List<ArticleDto>
    ) : Response
}
