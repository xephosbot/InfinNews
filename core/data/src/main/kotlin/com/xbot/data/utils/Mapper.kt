package com.xbot.data.utils

import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.SourceEntity
import com.xbot.domain.model.Article
import com.xbot.domain.model.Source
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal fun ArticleDto.toEntity() = ArticleEntity(
    source = SourceEntity(
        sourceId = source.id,
        name = source.name
    ),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
)

@OptIn(ExperimentalTime::class)
internal fun ArticleEntity.toDomain() = Article(
    id = id,
    source = Source(
        id = source.sourceId,
        name = source.name
    ),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = Instant.parse(publishedAt).toLocalDateTime(TimeZone.currentSystemDefault()),
    content = content,
)