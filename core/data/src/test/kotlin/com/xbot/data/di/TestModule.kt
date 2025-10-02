package com.xbot.data.di

import androidx.room.Room
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.local.ArticleDao
import com.xbot.data.datasource.local.RemoteKeysDao
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.models.dto.Response
import com.xbot.data.utils.TestDataFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.net.HttpURLConnection

internal val jsonModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    }
}

internal val networkTestModule = module {
    includes(jsonModule)
    single<MockWebServer> {
        val responseSuccess = TestDataFactory.fromJson<Response.Success>(TestDataFactory.readJsonFile("response_success.json"))
        MockWebServer().apply {
            dispatcher = object : Dispatcher() {
                override fun dispatch(request: RecordedRequest): MockResponse {
                    return when {
                        request.path?.contains("top-headlines") == true -> {
                            val url = request.requestUrl ?: return MockResponse()
                                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)

                            val page = url.queryParameter("page")?.toIntOrNull() ?: 1
                            val pageSize = url.queryParameter("pageSize")?.toIntOrNull() ?: 10

                            if (pageSize <= 100) {
                                val from = (page - 1) * pageSize
                                val to = minOf(from + pageSize, responseSuccess.totalResults)

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
                                    .setBody(get<Json>().encodeToString(response))
                            } else {
                                MockResponse()
                                    .setResponseCode(429)
                                    .setBody(TestDataFactory.readJsonFile("response_error.json"))
                            }
                        }
                        else -> {
                            MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                        }
                    }
                }
            }
        }
    }
    single<NewsService> {
        Retrofit.Builder()
            .baseUrl(get<MockWebServer>().url("/"))
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .build()
            .create(NewsService::class.java)
    }
}

internal val databaseTestModule = module {
    includes(jsonModule)
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(
            androidContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }
    single<ArticleDao> {
        get<AppDatabase>().articleDao()
    }
    single<RemoteKeysDao> {
        get<AppDatabase>().remoteKeysDao()
    }
}