package com.xbot.data

import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.dto.ArticleDto
import com.xbot.data.models.dto.Response
import com.xbot.data.models.dto.SourceDto
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.nio.charset.StandardCharsets
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NewsServiceTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: NewsService

    private val format = Json {
        ignoreUnknownKeys = true
        isLenient = true
        prettyPrint = true
    }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(format.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NewsService::class.java)
    }

    @After
    fun release() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test success response`() = runTest {
        val body = readJson("response_success.json")
        val response = MockResponse().setBody(body).setResponseCode(200)
        mockWebServer.enqueue(response)

        val result = service.getTopHeadlines(NewsCategory.TECHNOLOGY.toString(), 10, 1)
        val expectedArticle = ArticleDto(
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
        )

        assertEquals(expectedArticle, (result as Response.Success).articles.first())
    }

    @Test
    fun `test error response`() = runTest {
        val body = readJson("response_error.json")
        val response = MockResponse().setBody(body).setResponseCode(429)
        mockWebServer.enqueue(response)

        val exception = assertFailsWith<HttpException> {
            service.getTopHeadlines(NewsCategory.TECHNOLOGY.toString(), 10, 1)
        }

        assertEquals(429, exception.code())

        val errorBody = exception.response()?.errorBody()?.string()!!
        val error = format.decodeFromString<Response.Error>(errorBody)
        val expectedError = Response.Error(
            code = "apiKeyMissing",
            message = "Your API key is missing. Append this to the URL with the apiKey param, or use the x-api-key HTTP header."
        )

        assertEquals(expectedError, error)
    }

    private fun readJson(fileName: String): String {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        return inputStream.source().buffer().readString(StandardCharsets.UTF_8)
    }
}