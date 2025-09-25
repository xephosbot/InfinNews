@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.xbot.designsystem.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TabPosition
import androidx.compose.material3.rippleOrFallbackImplementation
import androidx.compose.material3.tokens.PrimaryNavigationTabTokens
import androidx.compose.material3.value
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import kotlin.math.max

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
    unselectedContentColor: Color = MaterialTheme.colorScheme.onSurface,
    interactionSource: MutableInteractionSource? = null
) {
    val styledText: @Composable (() -> Unit)? = text?.let {
        @Composable {
            val style = PrimaryNavigationTabTokens.LabelTextFont.value
                .copy(textAlign = TextAlign.Center)
            ProvideTextStyle(style, content = text)
        }
    }
    Tab(
        selected,
        onClick,
        modifier,
        enabled,
        selectedContentColor,
        unselectedContentColor,
        interactionSource
    ) {
        TabBaselineLayout(icon = icon, text = styledText)
    }
}

@Composable
private fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    selectedContentColor: Color = LocalContentColor.current,
    unselectedContentColor: Color = selectedContentColor,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    // The color of the Ripple should always the selected color, as we want to show the color
    // before the item is considered selected, and hence before the new contentColor is
    // provided by TabTransition.
    val ripple = rippleOrFallbackImplementation(bounded = false, color = selectedContentColor)

    TabTransition(selectedContentColor, unselectedContentColor, selected) {
        Column(
            modifier = modifier
                .selectable(
                    selected = selected,
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Tab,
                    interactionSource = interactionSource,
                    indication = ripple
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
private fun TabTransition(
    activeColor: Color,
    inactiveColor: Color,
    selected: Boolean,
    content: @Composable () -> Unit
) {
    val transition = updateTransition(selected, label = "")
    val color by transition.animateColor(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(
                    durationMillis = androidx.compose.material3.TabFadeInAnimationDuration,
                    delayMillis = androidx.compose.material3.TabFadeInAnimationDelay,
                    easing = LinearEasing
                )
            } else {
                tween(
                    durationMillis = androidx.compose.material3.TabFadeOutAnimationDuration,
                    easing = LinearEasing
                )
            }
        }, label = ""
    ) {
        if (it) activeColor else inactiveColor
    }
    CompositionLocalProvider(
        LocalContentColor provides color,
        content = content
    )
}

@Composable
private fun TabBaselineLayout(
    text: @Composable (() -> Unit)?,
    icon: @Composable (() -> Unit)?
) {
    Layout(
        {
            if (text != null) {
                Box(
                    Modifier
                        .layoutId("text")
                        .padding(horizontal = HorizontalTextPadding)
                ) { text() }
            }
            if (icon != null) {
                Box(Modifier.layoutId("icon")) { icon() }
            }
        }
    ) { measurables, constraints ->
        val textPlaceable = text?.let {
            measurables.fastFirst { it.layoutId == "text" }.measure(
                // Measure with loose constraints for height as we don't want the text to take up more
                // space than it needs
                constraints.copy(minHeight = 0)
            )
        }

        val iconPlaceable = icon?.let {
            measurables.fastFirst { it.layoutId == "icon" }.measure(constraints)
        }

        val tabWidth = max(textPlaceable?.width ?: 0, iconPlaceable?.width ?: 0)

        val specHeight = if (textPlaceable != null && iconPlaceable != null) {
            LargeTabHeight
        } else {
            SmallTabHeight
        }.roundToPx()

        val tabHeight = max(
            specHeight,
            (iconPlaceable?.height ?: 0) + (textPlaceable?.height ?: 0) +
                    IconDistanceFromBaseline.roundToPx()
        )

        val firstBaseline = textPlaceable?.get(FirstBaseline)
        val lastBaseline = textPlaceable?.get(LastBaseline)

        layout(tabWidth, tabHeight) {
            when {
                textPlaceable != null && iconPlaceable != null -> placeTextAndIcon(
                    density = this@Layout,
                    textPlaceable = textPlaceable,
                    iconPlaceable = iconPlaceable,
                    tabWidth = tabWidth,
                    tabHeight = tabHeight,
                    firstBaseline = firstBaseline!!,
                    lastBaseline = lastBaseline!!
                )
                textPlaceable != null -> placeTextOrIcon(textPlaceable, tabHeight)
                iconPlaceable != null -> placeTextOrIcon(iconPlaceable, tabHeight)
                else -> {
                }
            }
        }
    }
}

/**
 * Places the provided [textOrIconPlaceable] in the vertical center of the provided
 * [tabHeight].
 */
private fun Placeable.PlacementScope.placeTextOrIcon(
    textOrIconPlaceable: Placeable,
    tabHeight: Int
) {
    val contentY = (tabHeight - textOrIconPlaceable.height) / 2
    textOrIconPlaceable.placeRelative(0, contentY)
}

/**
 * Places the provided [textPlaceable] offset from the bottom of the tab using the correct
 * baseline offset, with the provided [iconPlaceable] placed above the text using the correct
 * baseline offset.
 */
private fun Placeable.PlacementScope.placeTextAndIcon(
    density: Density,
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    tabWidth: Int,
    tabHeight: Int,
    firstBaseline: Int,
    lastBaseline: Int
) {
    val baselineOffset = if (firstBaseline == lastBaseline) {
        SingleLineTextBaselineWithIcon
    } else {
        DoubleLineTextBaselineWithIcon
    }

    // Total offset between the last text baseline and the bottom of the Tab layout
    val textOffset = with(density) {
        baselineOffset.roundToPx() + PrimaryNavigationTabTokens.ActiveIndicatorHeight.roundToPx()
    }

    // How much space there is between the top of the icon (essentially the top of this layout)
    // and the top of the text layout's bounding box (not baseline)
    val iconOffset = with(density) {
        iconPlaceable.height + IconDistanceFromBaseline.roundToPx() - firstBaseline
    }

    val textPlaceableX = (tabWidth - textPlaceable.width) / 2
    val textPlaceableY = tabHeight - lastBaseline - textOffset
    textPlaceable.placeRelative(textPlaceableX, textPlaceableY)

    val iconPlaceableX = (tabWidth - iconPlaceable.width) / 2
    val iconPlaceableY = textPlaceableY - iconOffset
    iconPlaceable.placeRelative(iconPlaceableX, iconPlaceableY)
}

// Tab specifications
private val SmallTabHeight = PrimaryNavigationTabTokens.ContainerHeight
private val LargeTabHeight = 72.dp

// Tab transition specifications
private const val TabFadeInAnimationDuration = 150
private const val TabFadeInAnimationDelay = 100
private const val TabFadeOutAnimationDuration = 100

// The horizontal padding on the left and right of text
internal val HorizontalTextPadding = 16.dp

// Distance from the top of the indicator to the text baseline when there is one line of text and an
// icon
private val SingleLineTextBaselineWithIcon = 14.dp

// Distance from the top of the indicator to the last text baseline when there are two lines of text
// and an icon
private val DoubleLineTextBaselineWithIcon = 6.dp

// Distance from the first text baseline to the bottom of the icon in a combined tab
private val IconDistanceFromBaseline = 20.sp

// Distance from the end of the leading icon to the start of the text
private val TextDistanceFromLeadingIcon = 8.dp

fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    matchContentSize: Boolean = true,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val fraction = pagerState.currentPageOffsetFraction

        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)

        val (indicatorWidth, indicatorOffset) = when {
            (fraction > 0 && nextTab != null) -> {
                val currentOffset = if (matchContentSize) (currentTab.width - currentTab.contentWidth) / 2 else 0.dp
                val nextOffset = if (matchContentSize) (nextTab.width - nextTab.contentWidth) / 2 else 0.dp
                val currentTabWidth = if (matchContentSize) currentTab.contentWidth else currentTab.width
                val nextTabWidth = if (matchContentSize) nextTab.contentWidth else nextTab.width
                Pair(
                    lerp(currentTabWidth, nextTabWidth, fraction),
                    lerp(currentTab.left + currentOffset, nextTab.left + nextOffset, fraction)
                )
            }
            (fraction < 0 && previousTab != null) -> {
                val currentOffset = if (matchContentSize) (currentTab.width - currentTab.contentWidth) / 2 else 0.dp
                val previousOffset = if (matchContentSize) (previousTab.width - previousTab.contentWidth) / 2 else 0.dp
                val currentTabWidth = if (matchContentSize) currentTab.contentWidth else currentTab.width
                val previousTabWidth = if (matchContentSize) previousTab.contentWidth else previousTab.width
                Pair(
                    lerp(currentTabWidth, previousTabWidth, -fraction),
                    lerp(currentTab.left + currentOffset, previousTab.left + previousOffset, -fraction)
                )
            }
            else -> {
                val currentOffset = if (matchContentSize) (currentTab.width - currentTab.contentWidth) / 2 else 0.dp
                val currentTabWidth = if (matchContentSize) currentTab.contentWidth else currentTab.width
                Pair(
                    currentTabWidth,
                    currentTab.left + currentOffset
                )
            }
        }

        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth.roundToPx(),
                maxWidth = indicatorWidth.roundToPx(),
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )

        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.placeRelative(
                indicatorOffset.roundToPx(),
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}