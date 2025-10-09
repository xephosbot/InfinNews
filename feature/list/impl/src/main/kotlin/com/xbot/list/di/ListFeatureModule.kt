package com.xbot.list.di

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.composable
import com.xbot.common.navigation.NavEntryBuilder
import com.xbot.common.navigation.NavKey
import com.xbot.designsystem.utils.LocalAnimatedContentScope
import com.xbot.details.navigation.navigateToDetails
import com.xbot.list.ListScreen
import com.xbot.list.ListViewModel
import com.xbot.list.navigation.ListRoute
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val listFeatureModule = module {
    single<NavEntryBuilder>(named("feature/list")) {
        { navigator ->
            composable<ListRoute> {
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    ListScreen(
                        navigateToDetails = { article ->
                            navigator.navigateToDetails(article.url, article.id)
                        }
                    )
                }
            }
        }
    }
    single<NavKey>(named("startNavKey")) { ListRoute }
    viewModelOf(::ListViewModel)
}