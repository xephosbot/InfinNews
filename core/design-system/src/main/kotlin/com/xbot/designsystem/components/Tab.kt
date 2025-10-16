package com.xbot.designsystem.components

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.TabPosition
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

/**
 * Modifier to sync tab indicator with pager
 */
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
