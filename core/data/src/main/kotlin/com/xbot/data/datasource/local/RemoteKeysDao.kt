package com.xbot.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xbot.data.models.entity.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<RemoteKeys>)

    @Query("SELECT * FROM remote_keys WHERE articleUrl = :articleUrl")
    suspend fun getRemoteKeyByArticleUrl(articleUrl: String): RemoteKeys?

    @Query("DELETE FROM remote_keys WHERE category = :category")
    suspend fun deleteByCategory(category: String)
}