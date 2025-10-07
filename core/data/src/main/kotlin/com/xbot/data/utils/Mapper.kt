package com.xbot.data.utils

import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.SourceEntity
import com.xbot.domain.model.Article
import com.xbot.domain.model.Source
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal fun ArticleDto.toEntity(category: String) = ArticleEntity(
    source = SourceEntity(
        sourceId = source.id,
        name = source.name
    ),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = Instant.parse(publishedAt).toEpochMilliseconds(),
    content = content,
    category = category,
)

internal fun ArticleEntity.toDomain() = Article(
    source = Source(
        id = source.sourceId,
        name = source.name
    ),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = Instant.fromEpochMilliseconds(publishedAt)
        .toLocalDateTime(TimeZone.currentSystemDefault()),
    content = content,
)