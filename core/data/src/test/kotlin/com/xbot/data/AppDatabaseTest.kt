package com.xbot.data

import com.xbot.data.datasource.local.ArticleDao
import com.xbot.data.datasource.local.RemoteKeysDao
import com.xbot.data.di.dataTestModule
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKeys
import com.xbot.data.utils.TestDataFactory
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import kotlin.test.assertEquals

class AppDatabaseTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.Companion.create {
        modules(dataTestModule)
    }

    private val articleDao: ArticleDao by inject()
    private val remoteKeysDao: RemoteKeysDao by inject()

    @Test
    fun write_and_read_articles() = runTest {
        val articlesCount = 10
        val articles = TestDataFactory.articleEntities(articlesCount, NewsCategory.TECHNOLOGY)
        val articleUrls = articles.map(ArticleEntity::url)
        articleDao.insertAll(articles)

        repeat(articlesCount) { index ->
            val articleByUrl = articleDao.getArticleByUrl(articleUrls[index])
            assertEquals(articles[index], articleByUrl)
        }
    }

    @Test
    fun write_and_read_remote_keys() = runTest {
        val articlesCount = 10
        val articles = TestDataFactory.articleEntities(articlesCount, NewsCategory.TECHNOLOGY)
        val articleUrls = articles.map(ArticleEntity::url)
        val remoteKeys = articles.mapIndexed { index, article ->
            RemoteKeys(
                articleUrl = article.url,
                prevKey = index,
                nextKey = index + 1,
                category = article.category
            )
        }
        remoteKeysDao.insertAll(remoteKeys)

        repeat(articlesCount) { index ->
            val remoteKeyByUrl = remoteKeysDao.getRemoteKeyByArticleUrl(articleUrls[index])
            assertEquals(remoteKeys[index], remoteKeyByUrl)
        }
    }
}