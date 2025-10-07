package com.xbot.data.di

import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.local.TestAppDatabase
import com.xbot.data.datasource.remote.NewsServiceTestDispatcher
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val testOverrideModule = module {
    single<AppDatabase> {
        TestAppDatabase()
    }
    single<MockWebServer> {
        MockWebServer().apply {
            dispatcher = NewsServiceTestDispatcher()
        }
    }
    single<HttpUrl>(named("API_URL")) {
        get<MockWebServer>().url("/")
    }
}

val dataTestModule = module {
    includes(testOverrideModule, dataModule)
}