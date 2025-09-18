package com.xbot.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKey

@Database(
    entities = [ArticleEntity::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}