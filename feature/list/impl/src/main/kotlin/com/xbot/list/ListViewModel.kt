package com.xbot.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ListViewModel(
    private val repository: ArticleRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ListScreenState>(ListScreenState.Loading)
    val state: StateFlow<ListScreenState> = _state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ListScreenState.Loading
        )

    init {
        fetchArticles()
    }

    private fun fetchArticles() {
        viewModelScope.launch {
            _state.update { ListScreenState.Loading }
            val map = NewsCategory.entries.associateWith { category ->
                repository.getArticles(category).cachedIn(viewModelScope)
            }
            _state.update { ListScreenState.Success(map) }
        }
    }
}

internal sealed interface ListScreenState {
    data object Loading : ListScreenState
    data class Success(
        val categoriesPagingData: Map<NewsCategory, Flow<PagingData<Article>>>
    ) : ListScreenState
}