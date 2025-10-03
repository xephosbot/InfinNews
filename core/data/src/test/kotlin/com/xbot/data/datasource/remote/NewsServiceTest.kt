package com.xbot.data.datasource.remote

import com.xbot.data.di.dataTestModule
import com.xbot.data.models.dto.Response
import com.xbot.data.utils.JsonUtils
import com.xbot.data.utils.TestDataFactory
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import retrofit2.HttpException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NewsServiceTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(dataTestModule)
    }

    private val mockWebServer: MockWebServer by inject()
    private val service: NewsService by inject()

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun release() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test success response`() = runTest {
        val articlesCount = 10
        val result = service.getTopHeadlines(NewsCategory.TECHNOLOGY.toString(), articlesCount, 1)
        val expectedArticle = TestDataFactory.articleDtos(1)

        assertEquals(expectedArticle.first(), result.articles.first())
        assertEquals(articlesCount, result.articles.size)
    }

    @Test
    fun `test error response`() = runTest {
        val exception = assertFailsWith<HttpException> {
            service.getTopHeadlines(NewsCategory.TECHNOLOGY.toString(), 101, 1)
        }

        assertEquals(429, exception.code())

        val errorBody = exception.response()?.errorBody()?.string()!!
        val error = JsonUtils.fromJson<Response.Error>(errorBody)
        val expectedError = Response.Error(
            code = "pageSizeTooBig",
            message = "The maximum value of the pageSize param is 100 articles. You have requested 101."
        )

        assertEquals(expectedError, error)
    }
}