package com.xbot.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKeys

@Database(
    entities = [ArticleEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}