package com.xbot.data.datasource.paging

import android.os.Build
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.test.core.app.ApplicationProvider
import com.xbot.data.datasource.local.AppDatabase
import com.xbot.data.datasource.remote.NewsService
import com.xbot.data.di.databaseTestModule
import com.xbot.data.di.networkTestModule
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.domain.Error
import com.xbot.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalPagingApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.TIRAMISU])
class ArticleRemoteMediatorTest : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(databaseTestModule)
        modules(networkTestModule)
    }

    private val mockWebServer: MockWebServer by inject()
    private val service: NewsService by inject()
    private val db: AppDatabase by inject()

    @Before
    fun setup() {
        mockWebServer.start()
    }

    @After
    fun release() {
        mockWebServer.shutdown()
        db.clearAllTables()
        db.close()
    }

    @Test
    fun `refresh load returns success`() = runTest {
        val remoteMediator = ArticleRemoteMediator(db, service, NewsCategory.GENERAL)
        val pagingState = PagingState<Int, ArticleEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(20),
            leadingPlaceholderCount = 20
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse(result.endOfPaginationReached)
    }

    @Test
    fun `refresh load returns error`() = runTest {
        val remoteMediator = ArticleRemoteMediator(db, service, NewsCategory.GENERAL)
        val pagingState = PagingState<Int, ArticleEntity>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(101),
            leadingPlaceholderCount = 101
        )

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)
        assertTrue(result.throwable is Error.HttpError)
    }
}