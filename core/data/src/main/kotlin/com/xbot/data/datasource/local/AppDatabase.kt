package com.xbot.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import com.xbot.data.models.entity.ArticleEntity
import com.xbot.data.models.entity.RemoteKeys
import com.xbot.data.utils.InstantConverter
import com.xbot.data.utils.NewsCategoryConverter

@Database(
    entities = [ArticleEntity::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(NewsCategoryConverter::class, InstantConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    open suspend fun <R> withTransaction(block: suspend () -> R): R {
        return (this as RoomDatabase).withTransaction(block)
    }
}
