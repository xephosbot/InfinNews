package com.xbot.data.models.entity

import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "articles", primaryKeys = ["url", "category"])
internal data class ArticleEntity(
    val url: String,
    @Embedded
    val source: SourceEntity,
    val author: String?,
    val title: String,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: Long,
    val content: String?,
    val category: String,
)