package com.xbot.common.event

import androidx.compose.runtime.Stable

@Stable
data class SnackbarData<T>(
    val value: T,
    val onActionPerformed: () -> Unit,
    val onDismissed: () -> Unit,
)
