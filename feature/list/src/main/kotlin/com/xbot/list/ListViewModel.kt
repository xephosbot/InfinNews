package com.xbot.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat

@OptIn(ExperimentalCoroutinesApi::class)
class ListViewModel(repository: ArticleRepository) : ViewModel() {
    private val category = MutableStateFlow(NewsCategory.TECHNOLOGY)
    val articles: Flow<PagingData<Article>> = category.flatMapConcat { category ->
        repository.getArticles(category)
    }.cachedIn(viewModelScope)
}