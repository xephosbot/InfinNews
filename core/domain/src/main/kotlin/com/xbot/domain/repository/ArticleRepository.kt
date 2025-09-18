package com.xbot.domain.repository

import androidx.paging.PagingData
import com.xbot.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticles(query: String): Flow<PagingData<Article>>
}