package com.xbot.data.datasource.local

import androidx.paging.PagingSource
import com.xbot.data.models.entity.ArticleEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class TestArticleDao : ArticleDao {

    private val entitiesStateFlow = MutableStateFlow(emptyList<ArticleEntity>())

    override suspend fun insertAll(articles: List<ArticleEntity>) {
        entitiesStateFlow.update { oldValues ->
            (articles + oldValues).distinctBy { it.url + it.category }
        }
    }

    override fun pagingSource(category: String): PagingSource<Int, ArticleEntity> =
        throw NotImplementedError("Unused in tests")

    override suspend fun getArticleByUrl(articleUrl: String): ArticleEntity {
        return entitiesStateFlow.value.first { it.url == articleUrl }
    }

    override suspend fun deleteByCategory(category: String) {
        entitiesStateFlow.update { entities ->
            entities.filterNot { it.category == category }
        }
    }
}