package com.xbot.infinnews.ui

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.xbot.common.di.koinInjectAll
import com.xbot.common.navigation.NavEntryBuilder
import com.xbot.common.navigation.NavKey
import com.xbot.designsystem.utils.LocalSharedTransitionScope
import com.xbot.infinnews.navigation.InfinNewsNavigator
import com.xbot.infinnews.navigation.rememberInfinNewsNavigator
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun InfinNewsApp(
    modifier: Modifier = Modifier,
    navigator: InfinNewsNavigator = rememberInfinNewsNavigator(),
    startNavKey: NavKey = koinInject(named("startNavKey")),
    navEntryBuilders: List<NavEntryBuilder> = koinInjectAll()
) {
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            NavHost(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                navController = navigator.navController,
                startDestination = startNavKey
            ) {
                navEntryBuilders.forEach { builder ->
                    builder(navigator)
                }
            }
        }
    }
}