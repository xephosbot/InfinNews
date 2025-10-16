package com.xbot.designsystem.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

@Composable
fun Modifier.shimmer(durationMillis: Int = 1000): Modifier {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer transition")
    val offset by transition.animateFloat(
        initialValue = (size.width * -1).toFloat(),
        targetValue = (size.width * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer offset"
    )

    return this then Modifier.drawWithCache {
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.LightGray.copy(alpha = 0.2f),
                Color.LightGray.copy(alpha = 1.0f),
                Color.LightGray.copy(alpha = 0.2f),
            ),
            start = Offset(x = offset, y = offset),
            end = Offset(x = offset + size.width, y = offset + size.width),
        )

        onDrawBehind {
            drawRect(brush = brush)
        }
    }.onGloballyPositioned {
        size = it.size
    }
}
