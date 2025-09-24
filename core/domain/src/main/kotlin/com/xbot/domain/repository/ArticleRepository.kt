package com.xbot.domain.repository

import androidx.paging.PagingData
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {
    fun getArticles(category: NewsCategory): Flow<PagingData<Article>>
}