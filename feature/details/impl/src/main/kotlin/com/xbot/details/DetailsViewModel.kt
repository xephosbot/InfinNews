package com.xbot.details

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xbot.common.event.SnackbarData
import com.xbot.details.navigation.DetailsRoute
import com.xbot.domain.model.Article
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class DetailsViewModel(
    private val repository: ArticleRepository,
    private val route: DetailsRoute,
) : ViewModel() {
    private val _state = MutableStateFlow(DetailsScreenState(null, route.id))
    val state: StateFlow<DetailsScreenState> = _state
        .onStart {
            fetchArticle(route.url)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = _state.value
        )

    private val _events = Channel<DetailsScreenEvent>()
    val event: Flow<DetailsScreenEvent> = _events.receiveAsFlow()
        .distinctUntilChanged()

    private fun fetchArticle(articleUrl: String) {
        viewModelScope.launch {
            repository
                .getArticle(articleUrl)
                .fold(
                    onSuccess = { article ->
                        _state.update {
                            DetailsScreenState(
                                article = article,
                                articleId = route.id
                            )
                        }
                    },
                    onFailure = { error ->
                        showSnackbar(
                            SnackbarData(
                                value = error,
                                onDismissed = { /*Nothing to do*/ },
                                onActionPerformed = {
                                    fetchArticle(route.url)
                                }
                            )
                        )
                    }
                )
        }
    }

    private fun showSnackbar(data: SnackbarData<Throwable>) {
        Log.e("DetailsViewModel", data.value.stackTraceToString())
        viewModelScope.launch {
            _events.send(DetailsScreenEvent.ShowSnackbar(data))
        }
    }
}

@Stable
internal data class DetailsScreenState(
    val article: Article?,
    val articleId: String,
)

@Stable
internal sealed interface DetailsScreenEvent {
    @Stable
    data class ShowSnackbar(val data: SnackbarData<Throwable>) : DetailsScreenEvent
}
