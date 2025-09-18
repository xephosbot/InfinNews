package com.xbot.domain.model

import kotlinx.datetime.LocalDateTime

data class Article(
    val id: Long,
    val source: Source,
    val author: String? = null,
    val title: String,
    val description: String? = null,
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: LocalDateTime,
    val content: String? = null
)
