package com.xbot.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ArticleDto(
    @SerialName("source") val source: SourceDto,
    @SerialName("author") val author: String? = null,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("url") val url: String,
    @SerialName("urlToImage") val urlToImage: String? = null,
    @SerialName("publishedAt") val publishedAt: String,
    @SerialName("content") val content: String? = null
)