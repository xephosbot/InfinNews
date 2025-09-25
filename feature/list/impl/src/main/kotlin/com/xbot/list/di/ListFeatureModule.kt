package com.xbot.list.di

import androidx.navigation.compose.composable
import com.xbot.common.navigation.NavEntryBuilder
import com.xbot.common.navigation.NavKey
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
                ListScreen(
                    onArticleClick = { article ->
                        navigator.navigateToDetails(article.url)
                    }
                )
            }
        }
    }
    single<NavKey>(named("startNavKey")) {
        ListRoute
    }
    viewModelOf(::ListViewModel)
}