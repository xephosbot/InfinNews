package com.xbot.data.utils

import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.dto.Response
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.domain.model.NewsCategory

object TestDataFactory {

    internal fun articleEntities(count: Int, category: NewsCategory): List<ArticleEntity> {
        return articleDtos(count).map { it.toEntity(category.toString()) }
    }

    internal fun articleDtos(count: Int): List<ArticleDto> {
        val response = JsonUtils.fromJson<Response.Success>(JsonUtils.readJsonFile("response_success.json"))
        return response.articles.take(count)
    }
}