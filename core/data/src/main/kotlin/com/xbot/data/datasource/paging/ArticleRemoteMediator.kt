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
import com.xbot.domain.Error
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

            Log.d(this::class.java.simpleName, "PAGE: $page, LOAD TYPE: $loadType, PAGE SIZE: ${state.config.pageSize}")

            val response = service.getTopHeadlines(
                category = category.toString(),
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
        } catch (e: Exception) {
            if (e is CancellationException) {
                throw e
            } else {
                val error = when (e) {
                    is UnknownHostException, is SocketTimeoutException -> Error.NetworkError(e)
                    is SerializationException -> Error.SerializationError(e.message)
                    is HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        val error = errorBody?.let { get<Json>().decodeFromString<Response.Error>(it) }
                        Error.HttpError(e.code(), error?.message)
                    }
                    is IOException -> Error.IOError(e)
                    else -> Error.Unknown(e)
                }
                MediatorResult.Error(error)
            }
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