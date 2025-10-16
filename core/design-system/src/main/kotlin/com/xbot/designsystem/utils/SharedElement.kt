package com.xbot.designsystem.utils

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.layout.approachLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.round

/**
 * Copy from Android Developers Youtube channel: https://youtu.be/0moEXBqNDZI
 */
@Composable
@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedBoundsRevealWithShapeMorph(
    sharedContentState: SharedTransitionScope.SharedContentState,
    sharedTransitionScope: SharedTransitionScope = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope = LocalAnimatedContentScope.current,
    resizeMode: SharedTransitionScope.ResizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    restingShapeCornerRadius: Dp = 0.dp,
    targetShapeCornerRadius: Dp = 0.dp,
    renderInOverlayDuringTransition: Boolean = true,
    keepChildrenSizePlacement: Boolean = true,
): Modifier {
    with(sharedTransitionScope) {
        val density = LocalDensity.current
        val animatedProgress by animatedVisibilityScope.transition.animateFloat(label = "progress") {
            when (it) {
                EnterExitState.PreEnter -> 1f
                EnterExitState.Visible -> 0f
                EnterExitState.PostExit -> 1f
            }
        }

        val clipShape = remember {
            GenericShape { size, _ ->
                val radius = with(density) {
                    lerp(restingShapeCornerRadius, targetShapeCornerRadius, animatedProgress).toPx()
                }
                addRoundRect(RoundRect(size.toRect(), CornerRadius(radius)))
            }
        }
        val modifier = if (keepChildrenSizePlacement) {
            Modifier
                .skipToLookaheadSize()
                .skipToLookaheadPosition()
        } else {
            Modifier
        }

        return this@sharedBoundsRevealWithShapeMorph
            .sharedBounds(
                sharedContentState = sharedContentState,
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = resizeMode,
                clipInOverlayDuringTransition = OverlayClip(clipShape),
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            )
            .then(modifier)
    }
}

/**
 * Copy from Compose 1.9
 */
context(SharedTransitionScope)
@ExperimentalSharedTransitionApi
fun Modifier.skipToLookaheadPosition(
    enabled: () -> Boolean = { isTransitionActive }
): Modifier =
    this.approachLayout(
        isMeasurementApproachInProgress = { false },
        isPlacementApproachInProgress = { enabled() },
    ) { m, c ->
        m.measure(c).run {
            layout(width, height) {
                if (enabled()) {
                    coordinates?.let {
                        val target = lookaheadScopeCoordinates.localLookaheadPositionOf(it)
                        val actual = lookaheadScopeCoordinates.localPositionOf(it)
                        val delta = target - actual

                        val offset =
                            it.localPositionOf(lookaheadScopeCoordinates, delta) -
                                it.localPositionOf(lookaheadScopeCoordinates)

                        place(offset.round())
                    } ?: place(0, 0)
                } else {
                    place(0, 0)
                }
            }
        }
    }

data class ArticleSharedElementKey(val id: String)

@ExperimentalSharedTransitionApi
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    error("LocalSharedTransitionScope not provided")
}

val LocalAnimatedContentScope = compositionLocalOf<AnimatedContentScope> {
    error("LocalAnimatedContentScope not provided")
}
