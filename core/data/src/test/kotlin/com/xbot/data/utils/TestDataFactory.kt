package com.xbot.data.utils

import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.dto.Response
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.domain.model.NewsCategory
import kotlinx.serialization.json.Json
import okio.buffer
import okio.source
import org.koin.test.KoinTest
import org.koin.test.get

object TestDataFactory : KoinTest {

    internal fun articleEntities(count: Int, category: NewsCategory): List<ArticleEntity> {
        return articleDtos(count).map { it.toEntity(category.toString()) }
    }

    internal fun articleDtos(count: Int): List<ArticleDto> {
        val response = fromJson<Response.Success>(readJsonFile("response_success.json"))
        return response.articles.take(count)
    }

    internal inline fun <reified T> fromJson(json: String): T {
        return get<Json>().decodeFromString(json)
    }

    internal fun readJsonFile(fileName: String): String {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        return inputStream.source().buffer().readString(Charsets.UTF_8)
    }
}