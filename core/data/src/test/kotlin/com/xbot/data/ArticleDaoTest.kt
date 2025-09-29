package com.xbot.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.local.ArticleDao
import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.dto.SourceDto
import com.xbot.data.utils.toEntity
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ArticleDaoReadWriteTest {
    private lateinit var db: AppDatabase
    private lateinit var articleDao: ArticleDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room
            .inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .build()
        articleDao = db.articleDao()
    }

    @After
    fun release() {
        db.close()
    }

    @Test
    fun `write article and read in list`() = runTest {
        val article =  ArticleDto(
            source = SourceDto(
                id = null,
                name = "Ambcrypto.com"
            ),
            author = "Samyukhtha L KM",
            title = "Ethereum buyers drain exchanges, sellers hold the line – Who breaks first? - AMBCrypto",
            description = "Why Ethereum’s silence feels louder than any rally.",
            url = "https://ambcrypto.com/?p=532404",
            urlToImage = "https://ambcrypto.com/wp-content/uploads/2025/09/Samyukhtha-6-2-1000x600.webp",
            publishedAt = "2025-09-29T01:06:02Z",
            content = "Key Takeaways\r\nWhy is Ethereum’s price stuck?\r\nBecause current buying is being matched by selling, keeping ETH flat even as reserves drop.\r\nCould Ethereum see a short squeeze soon?\r\nWith most downsid… [+2137 chars]",
        ).toEntity(NewsCategory.TECHNOLOGY.toString())

        articleDao.insertAll(listOf(article))
        val articleByUrl = articleDao.getArticleByUrl("https://ambcrypto.com/?p=532404")

        assertEquals(article, articleByUrl)
    }
}