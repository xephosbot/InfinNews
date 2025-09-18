package com.xbot.data.datasource.local

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import arrow.core.getOrElse
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKey
import com.xbot.data.utils.toEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
internal class ArticleRemoteMediator(
    private val database: AppDatabase,
    private val service: NewsService,
    private val query: String,
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = database.withTransaction {
                        database.remoteKeyDao().remoteKeyByQuery(query)
                    }
                    if (remoteKey?.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    remoteKey.nextKey
                }
            }

            Log.e("TAGGGGGGG", "PAGE: $page, LOAD TYPE: $loadType")

            val response = service.getEverything(
                query = query,
                page = page,
                pageSize = when (loadType) {
                    LoadType.REFRESH -> state.config.initialLoadSize
                    else -> state.config.pageSize
                }
            ).getOrElse {
                return MediatorResult.Error(it)
            }

            val items = response.articles.map { it.toEntity() }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().deleteByQuery(query)
                    database.articleDao().deleteByQuery(query)
                }

                database.remoteKeyDao().insertOrReplace(
                    RemoteKey(
                        query = query,
                        nextKey = page + 1,
                    )
                )
                database.articleDao().insertAll(items)
            }

            MediatorResult.Success(endOfPaginationReached = items.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}