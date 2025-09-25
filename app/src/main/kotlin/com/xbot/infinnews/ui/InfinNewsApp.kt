package com.xbot.infinnews.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.xbot.common.di.koinInjectAll
import com.xbot.common.navigation.NavEntryBuilder
import com.xbot.common.navigation.NavKey
import com.xbot.infinnews.navigation.InfinNewsNavigator
import com.xbot.infinnews.navigation.rememberInfinNewsNavigator
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
internal fun InfinNewsApp(
    modifier: Modifier = Modifier,
    navigator: InfinNewsNavigator = rememberInfinNewsNavigator(),
    startNavKey: NavKey = koinInject(named("startNavKey")),
    navEntryBuilders: List<NavEntryBuilder> = koinInjectAll()
) {
    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navigator.navController,
        startDestination = startNavKey
    ) {
        navEntryBuilders.forEach { builder ->
            builder(navigator)
        }
    }
}