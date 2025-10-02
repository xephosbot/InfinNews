package com.xbot.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.paging.ArticleRemoteMediator
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.utils.toDomain
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
internal class DefaultArticleRepository(
    private val database: AppDatabase,
    private val service: com.xbot.data.datasource.remote.NewsService,
) : ArticleRepository {
    override fun getArticles(category: NewsCategory): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = PAGE_SIZE,
            ),
            remoteMediator = ArticleRemoteMediator(
                database = database,
                service = service,
                category = category
            ),
            pagingSourceFactory = {
                database.articleDao().pagingSource(category.toString())
            }
        ).flow.map { pagingData ->
            pagingData.map(ArticleEntity::toDomain)
        }
    }

    override suspend fun getArticle(articleUrl: String): Article {
        return database.articleDao().getArticleByUrl(articleUrl).toDomain()
    }

    companion object {
        const val PAGE_SIZE = 10
    }
}