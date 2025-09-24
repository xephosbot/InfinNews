package com.xbot.data.datasource.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.dto.Response
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKeys
import com.xbot.data.utils.toEntity
import com.xbot.domain.model.NewsCategory
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
internal class ArticleRemoteMediator(
    private val database: AppDatabase,
    private val service: NewsService,
    private val category: NewsCategory,
) : RemoteMediator<Int, ArticleEntity>() {

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

            Log.d(this::class.java.simpleName, "PAGE: $page, LOAD TYPE: $loadType, PAGE SIZE: ${state.config.pageSize}")

            val response = service.getTopHeadlines(
                category = category.toString(),
                page = page,
                pageSize = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            )

            when (response) {
                is Response.Error -> {
                    MediatorResult.Error(RuntimeException(response.message))
                }
                is Response.Success -> {
                    val articles = response.articles
                    val endOfPaginationReached = articles.isEmpty() || articles.size < state.config.pageSize.minus(1)

                    database.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            remoteKeysDao.deleteByCategory(category.toString())
                            articleDao.deleteByCategory(category.toString())
                        }

                        val prevKey = if (page == 1) null else page - 1
                        val nextKey = if (endOfPaginationReached) null else page + 1

                        val remoteKeys = articles.map { article ->
                            RemoteKeys(
                                articleUrl = article.url,
                                prevKey = prevKey,
                                nextKey = nextKey,
                                category = category.toString()
                            )
                        }

                        val articleEntities = articles.map { article ->
                            article.toEntity(category.toString())
                        }

                        remoteKeysDao.insertAll(remoteKeys)
                        articleDao.insertAll(articleEntities)
                    }

                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
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