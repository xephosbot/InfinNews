package com.xbot.data.datasource.local

import com.xbot.data.models.entity.RemoteKeys
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class TestRemoteKeysDao : RemoteKeysDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<RemoteKeys>())

    override suspend fun insertAll(keys: List<RemoteKeys>) {
        entitiesStateFlow.update { oldValues ->
            (keys + oldValues).distinctBy { it.articleUrl + it.category }
        }
    }

    override suspend fun getRemoteKeyByArticleUrl(articleUrl: String): RemoteKeys? {
        return entitiesStateFlow.value.firstOrNull { it.articleUrl == articleUrl }
    }

    override suspend fun deleteByCategory(category: NewsCategory) {
        entitiesStateFlow.update { keys ->
            keys.filterNot { it.category == category }
        }
    }
}
