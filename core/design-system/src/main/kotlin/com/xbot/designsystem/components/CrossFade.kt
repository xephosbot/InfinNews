package com.xbot.designsystem.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Copy from Compose 1.9 for expose contentKey argument
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> Crossfade(
    targetState: T,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    contentKey: (targetState: T) -> Any? = { it },
    label: String = "Crossfade",
    content: @Composable (T) -> Unit
) {
    val transition = updateTransition(targetState, label)
    transition.Crossfade(modifier, animationSpec, contentKey, content = content)
}