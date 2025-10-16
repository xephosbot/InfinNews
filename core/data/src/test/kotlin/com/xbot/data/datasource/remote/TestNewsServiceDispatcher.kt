package com.xbot.data.datasource.remote

import com.xbot.data.models.dto.Response
import com.xbot.data.utils.JsonUtils
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.koin.test.KoinTest
import java.net.HttpURLConnection
import kotlin.math.min

internal class TestNewsServiceDispatcher : Dispatcher(), KoinTest {
    private val responseSuccess: Response.Success =
        JsonUtils.fromJson(JsonUtils.readJsonFile("response_success.json"))

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when {
            request.path?.contains("top-headlines") == true -> {
                val url = request.requestUrl ?: return MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)

                val page = url.queryParameter("page")?.toIntOrNull() ?: 1
                val pageSize = url.queryParameter("pageSize")?.toIntOrNull() ?: 10

                if (pageSize <= 100) {
                    val from = (page - 1) * pageSize
                    val to = min(from + pageSize, responseSuccess.totalResults)

                    val pageData = if (from in responseSuccess.articles.indices) {
                        responseSuccess.articles.subList(from, to)
                    } else {
                        emptyList()
                    }

                    val response = Response.Success(
                        totalResults = responseSuccess.totalResults,
                        articles = pageData
                    )

                    MockResponse()
                        .setResponseCode(HttpURLConnection.HTTP_OK)
                        .setBody(JsonUtils.toJson(response))
                } else {
                    MockResponse()
                        .setResponseCode(429)
                        .setBody(JsonUtils.readJsonFile("response_error.json"))
                }
            }
            else -> {
                MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
            }
        }
    }
}
