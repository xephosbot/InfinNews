package com.xbot.data.datasource.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xbot.data.models.entity.ArticleEntity

@Dao
internal interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles WHERE title LIKE :query  ORDER BY publishedAt DESC")
    fun pagingSource(query: String): PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM articles WHERE title LIKE :query")
    suspend fun deleteByQuery(query: String)

    @Query("SELECT COUNT(*) FROM articles WHERE title LIKE :query")
    suspend fun countByQuery(query: String): Int
}