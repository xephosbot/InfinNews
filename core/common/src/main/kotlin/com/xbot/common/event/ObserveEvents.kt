package com.xbot.common.event

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> Flow<T>.ObserveEvents(onEvent: suspend (T) -> Unit) {
    LaunchedEffect(this) {
        this@ObserveEvents.collect(onEvent)
    }
}
