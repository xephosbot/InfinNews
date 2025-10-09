package com.xbot.designsystem.components

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey

inline fun <T : Any> LazyListScope.pagingItems(
    items: LazyPagingItems<T>,
    noinline key: ((index: Int) -> Any)? = items.itemKey(),
    crossinline itemContent: @Composable LazyItemScope.(index: Int, item: T?) -> Unit,
) {
    items(
        count = items.itemCount,
        key = key,
        contentType = items.itemContentType { "Paging Items" },
    ) {
        itemContent(it, items[it])
    }
}

@Composable
fun <T : Any> LazyPagingItems<T>.observeLoadState(
    onError: (Throwable) -> Unit
) {
    LaunchedEffect(this) {
        snapshotFlow { loadState }.collect { loadState ->
            (loadState.refresh as? LoadState.Error)?.let { onError(it.error) }
            (loadState.append as? LoadState.Error)?.let { onError(it.error) }
            (loadState.prepend as? LoadState.Error)?.let { onError(it.error) }
        }
    }
}