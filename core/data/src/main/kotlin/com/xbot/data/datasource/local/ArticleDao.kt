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

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    fun pagingSource(category: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE url = :articleUrl")
    suspend fun getArticleByUrl(articleUrl: String): ArticleEntity

    @Query("DELETE FROM articles WHERE category = :category")
    suspend fun deleteByCategory(category: String)
}