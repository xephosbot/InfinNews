package com.xbot.data.di

import androidx.room.Room
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.local.ArticleDao
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.repository.DefaultArticleRepository
import com.xbot.data.utils.Urls
import com.xbot.domain.repository.ArticleRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

private val networkModule = module {
    single<Json> {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }
    }

    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .build()
    }

    single<NewsService> {
        Retrofit.Builder()
            .baseUrl(Urls.BASE_URL)
            .addConverterFactory(get<Json>().asConverterFactory("application/json".toMediaType()))
            .client(get<OkHttpClient>())
            .build()
            .create(NewsService::class.java)
    }
}

private val databaseModule = module {
    single<AppDatabase> {
        Room.inMemoryDatabaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            //"news_database"
        ).build()
    }

    single<ArticleDao> {
        get<AppDatabase>().articleDao()
    }
}

private val repositoryModule = module {
    singleOf(::DefaultArticleRepository) { bind<ArticleRepository>() }
}

val dataModule = networkModule + databaseModule + repositoryModule