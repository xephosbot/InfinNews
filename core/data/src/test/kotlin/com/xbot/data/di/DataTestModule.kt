package com.xbot.data.di

import androidx.room.Room
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.utils.NewsServiceTestDispatcher
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockWebServer
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val testOverrideModule = module {
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(
            androidContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
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