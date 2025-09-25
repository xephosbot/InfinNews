package com.xbot.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

internal class ListViewModel(private val repository: ArticleRepository) : ViewModel() {
    private val categoryPagingData = mutableMapOf<NewsCategory, Flow<PagingData<Article>>>()

    fun getArticles(category: NewsCategory): Flow<PagingData<Article>> {
        return categoryPagingData.getOrPut(category) {
            repository.getArticles(category).cachedIn(viewModelScope)
        }
    }
}