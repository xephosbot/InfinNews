package com.xbot.details

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.xbot.details.navigation.DetailsRoute
import com.xbot.domain.model.Article
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class DetailsViewModel(
    private val repository: ArticleRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val articleUrl: String = savedStateHandle.toRoute<DetailsRoute>().url
    private val articleId: String = savedStateHandle.toRoute<DetailsRoute>().id

    private val articleFlow: Flow<Article> = flow { emit(repository.getArticle(articleUrl)) }
    val state: StateFlow<DetailsScreenState> = articleFlow
        .map { article ->
            DetailsScreenState(
                article = article,
                articleId = articleId
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DetailsScreenState(null, articleId)
        )
}

@Stable
internal data class DetailsScreenState(
    val article: Article?,
    val articleId: String,
)