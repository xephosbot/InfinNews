package com.xbot.data.models.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
internal data class ArticleEntity(
    @PrimaryKey
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