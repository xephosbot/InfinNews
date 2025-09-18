package com.xbot.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xbot.domain.model.Article
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow

class ListViewModel(repository: ArticleRepository) : ViewModel() {
    val articles: Flow<PagingData<Article>> = repository.getArticles("%bitcoin%")
        .cachedIn(viewModelScope)
}