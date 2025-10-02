package com.xbot.data.datasource.local

import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.xbot.data.di.databaseTestModule
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKeys
import com.xbot.data.utils.TestDataFactory
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class AppDatabaseTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(databaseTestModule)
    }

    private val db: AppDatabase by inject()
    private val articleDao: ArticleDao by inject()
    private val remoteKeysDao: RemoteKeysDao by inject()

    @After
    fun release() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `write and read articles`() = runTest {
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
    fun `write and read remote keys`() = runTest {
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