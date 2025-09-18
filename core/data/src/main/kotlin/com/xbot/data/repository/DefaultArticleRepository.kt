package com.xbot.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.local.ArticleRemoteMediator
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.utils.toDomain
import com.xbot.domain.model.Article
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalPagingApi::class)
internal class DefaultArticleRepository(
    private val database: AppDatabase,
    private val service: NewsService,
) : ArticleRepository {
    override fun getArticles(query: String): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = PAGE_SIZE,
                jumpThreshold = PAGE_SIZE * 3,
            ),
            remoteMediator = ArticleRemoteMediator(database, service, query),
            pagingSourceFactory = { database.articleDao().pagingSource(query) }
        ).flow.map { pagingData ->
            pagingData.map(ArticleEntity::toDomain)
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}