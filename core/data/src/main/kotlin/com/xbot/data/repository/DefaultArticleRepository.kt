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
import com.xbot.data.utils.Constants.DEFAULT_PAGE_SIZE
import com.xbot.data.utils.toDomain
import com.xbot.data.utils.toDomainError
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalPagingApi::class)
internal class DefaultArticleRepository(
    private val database: AppDatabase,
    private val service: NewsService,
) : ArticleRepository, KoinComponent {
    override fun getArticles(category: NewsCategory): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = DEFAULT_PAGE_SIZE,
                prefetchDistance = DEFAULT_PAGE_SIZE,
                enablePlaceholders = true,
                initialLoadSize = DEFAULT_PAGE_SIZE,
            ),
            remoteMediator = ArticleRemoteMediator(
                database = database,
                service = service,
                category = category
            ),
            pagingSourceFactory = {
                database.articleDao().pagingSource(category)
            }
        ).flow.map { pagingData ->
            pagingData.map(ArticleEntity::toDomain)
        }
    }

    override suspend fun getArticle(articleUrl: String): Result<Article> {
        return try {
            Result.success(database.articleDao().getArticleByUrl(articleUrl).toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e.toDomainError())
        }
    }
}
