package com.xbot.data.utils

import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.dto.Response
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.SourceEntity
import com.xbot.domain.DomainError
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.model.Source
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun ArticleDto.toEntity(category: NewsCategory) = ArticleEntity(
    source = SourceEntity(
        sourceId = source.id,
        name = source.name
    ),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = Instant.parse(publishedAt),
    content = content,
    category = category,
)

internal fun ArticleEntity.toDomain() = Article(
    id = url + category,
    source = Source(
        id = source.sourceId,
        name = source.name
    ),
    author = author,
    title = title,
    description = description,
    url = url,
    urlToImage = urlToImage,
    publishedAt = publishedAt.toLocalDateTime(TimeZone.currentSystemDefault()),
    content = content,
)

context(KoinComponent)
internal fun Exception.toDomainError() = when (val e = this) {
    is UnknownHostException, is SocketTimeoutException -> DomainError.NetworkError(e)
    is SerializationException -> DomainError.SerializationError(e.message)
    is HttpException -> {
        val errorBody = e.response()?.errorBody()?.string()
        val error = errorBody?.let { get<Json>().decodeFromString<Response.Error>(it) }
        DomainError.HttpError(e.code(), error?.message)
    }
    is IOException -> DomainError.IOError(e)
    else -> DomainError.Unknown(e)
}
