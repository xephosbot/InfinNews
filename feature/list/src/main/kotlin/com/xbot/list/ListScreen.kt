package com.xbot.list

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.xbot.domain.model.Article
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ListScreen(
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = koinViewModel()
) {
    val articles = viewModel.articles.collectAsLazyPagingItems()

    ListContent(
        modifier = modifier,
        items = articles,
    )
}

@Composable
private fun ListContent(
    modifier: Modifier = Modifier,
    items: LazyPagingItems<Article>
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val onShowErrorMessage: (Throwable) -> Unit = {
        scope.launch {
            val result = snackbarHostState.showSnackbar(
                message = it.message.orEmpty(),
                actionLabel = "Retry",
            )
            if (result == SnackbarResult.ActionPerformed) {
                items.refresh()
            }
        }
    }

    LaunchedEffect(items) {
        snapshotFlow { items.loadState }.collect { loadState ->
            (loadState.refresh as? LoadState.Error)?.let { onShowErrorMessage(it.error) }
            (loadState.append as? LoadState.Error)?.let { onShowErrorMessage(it.error) }
            (loadState.prepend as? LoadState.Error)?.let { onShowErrorMessage(it.error) }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding
        ) {
            pagingItems(
                items = items,
            ) { article ->
                ArticleItem(
                    modifier = Modifier.animateItem(),
                    article = article
                )
            }
        }
    }
}

@Composable
private fun ArticleItem(
    modifier: Modifier = Modifier,
    article: Article?
) {
    Crossfade(
        targetState = article,
        modifier = modifier
            .clickable {

            },
    ) { state ->
        when (state) {
            null -> {
                ListItem(
                    headlineContent = {
                        Text(text = "Loading...")
                    }
                )
            }
            else -> {
                ListItem(
                    headlineContent = {
                        Text(
                            text = state.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}

inline fun <T : Any> LazyListScope.pagingItems(
    items: LazyPagingItems<T>,
    noinline key: ((index: Int) -> Any)? = items.itemKey(),
    crossinline itemContent: @Composable LazyItemScope.(item: T?) -> Unit,
) {
    items(
        count = items.itemCount,
        key = key,
        contentType = items.itemContentType { "Paging Items" },
    ) {
        itemContent(items[it])
    }
}