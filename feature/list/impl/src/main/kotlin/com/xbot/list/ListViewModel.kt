package com.xbot.list

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.xbot.domain.model.Article
import com.xbot.domain.model.NewsCategory
import com.xbot.domain.repository.ArticleRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ListViewModel(
    private val repository: ArticleRepository
) : ViewModel() {
    private val _state = MutableStateFlow<ListScreenState>(ListScreenState.Loading)
    val state: StateFlow<ListScreenState> = _state.asStateFlow()

    private val _events = Channel<ListScreenEvent>()
    val event: Flow<ListScreenEvent> = _events.receiveAsFlow()

    fun onAction(action: ListScreenAction) {
        when (action) {
            is ListScreenAction.SelectCategory -> selectCategory(action.category)
            is ListScreenAction.ShowSnackbar -> showSnackbar(action.data)
        }
    }

    private fun selectCategory(category: NewsCategory) {
        viewModelScope.launch {
            val currentState = state.value
            val currentCategory = (currentState as? ListScreenState.Success)?.selectedCategory
            val existingFlows = (currentState as? ListScreenState.Success)?.pagingFlows
                ?: emptyMap()

            if (currentCategory == category) {
                return@launch
            }

            val flow = existingFlows[category] ?: repository
                .getArticles(category)
                .cachedIn(viewModelScope)

            _state.update {
                ListScreenState.Success(
                    selectedCategory = category,
                    pagingFlows = existingFlows + (category to flow)
                )
            }
        }
    }

    private fun showSnackbar(data: SnackbarData<Throwable>) {
        Log.e("ListViewModel", data.value.stackTraceToString())
        viewModelScope.launch {
            _events.send(ListScreenEvent.ShowSnackbar(data))
        }
    }
}

@Stable
internal sealed interface ListScreenState {
    @Stable
    data object Loading : ListScreenState
    @Stable
    data class Success(
        val selectedCategory: NewsCategory,
        val pagingFlows: Map<NewsCategory, Flow<PagingData<Article>>>
    ) : ListScreenState
}

@Stable
internal sealed interface ListScreenAction {
    @Stable
    data class SelectCategory(val category: NewsCategory): ListScreenAction
    @Stable
    data class ShowSnackbar(val data: SnackbarData<Throwable>) : ListScreenAction
}

@Stable
internal sealed interface ListScreenEvent {
    @Stable
    data class ShowSnackbar(val data: SnackbarData<Throwable>) : ListScreenEvent
}

@Stable
internal data class SnackbarData<T>(
    val value: T,
    val onActionPerformed: () -> Unit,
    val onDismissed: () -> Unit,
)