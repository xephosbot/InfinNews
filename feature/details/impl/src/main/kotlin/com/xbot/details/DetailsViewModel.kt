package com.xbot.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.xbot.details.navigation.DetailsRoute
import com.xbot.domain.model.Article
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DetailsViewModel(
    private val repository: ArticleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleUrl: String = savedStateHandle.toRoute<DetailsRoute>().url
    private val category: String = savedStateHandle.toRoute<DetailsRoute>().category

    private val _state = MutableStateFlow(DetailsScreenState(null, articleUrl, category))
    val state: StateFlow<DetailsScreenState> = _state
        .onStart {
            fetchArticle(articleUrl)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value
        )

    private fun fetchArticle(url: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(article = repository.getArticle(url))
            }
        }
    }
}

internal data class DetailsScreenState(
    val article: Article?,
    val articleUrl: String,
    val category: String,
)