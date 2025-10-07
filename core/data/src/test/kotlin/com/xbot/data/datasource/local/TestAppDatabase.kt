package com.xbot.data.datasource.local

import androidx.room.InvalidationTracker

internal class TestAppDatabase : AppDatabase() {

    override fun articleDao(): ArticleDao = TestArticleDao()

    override fun remoteKeysDao(): RemoteKeysDao = TestRemoteKeysDao()

    override fun createInvalidationTracker(): InvalidationTracker =
        throw NotImplementedError("Unused in tests")

    override fun clearAllTables() =
        throw NotImplementedError("Unused in tests")
}