package com.xbot.data.datasource.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKeys
import com.xbot.data.utils.toDomainError
import com.xbot.data.utils.toEntity
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.CancellationException
import org.koin.core.component.KoinComponent

@OptIn(ExperimentalPagingApi::class)
internal class ArticleRemoteMediator(
    private val database: AppDatabase,
    private val service: NewsService,
    private val category: NewsCategory,
) : RemoteMediator<Int, ArticleEntity>(), KoinComponent {

    private val articleDao = database.articleDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                    prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
            }

            val response = service.getTopHeadlines(
                category = category,
                page = page,
                pageSize = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )

            val articles = response.articles
            val endOfPaginationReached = articles.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.deleteByCategory(category)
                    articleDao.deleteByCategory(category)
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val remoteKeys = articles.map { article ->
                    RemoteKeys(
                        articleUrl = article.url,
                        prevKey = prevKey,
                        nextKey = nextKey,
                        category = category
                    )
                }

                val articleEntities = articles.map { article ->
                    article.toEntity(category)
                }

                remoteKeysDao.insertAll(remoteKeys)
                articleDao.insertAll(articleEntities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e.toDomainError())
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ArticleEntity>
    ): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { article ->
                remoteKeysDao.getRemoteKeyByArticleUrl(article.url)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, ArticleEntity>
    ): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article ->
                remoteKeysDao.getRemoteKeyByArticleUrl(article.url)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, ArticleEntity>
    ): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { article ->
                remoteKeysDao.getRemoteKeyByArticleUrl(article.url)
            }
    }
}
