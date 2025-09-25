package com.xbot.details.di

import androidx.navigation.compose.composable
import com.xbot.common.navigation.NavEntryBuilder
import com.xbot.details.DetailsScreen
import com.xbot.details.DetailsViewModel
import com.xbot.details.navigation.DetailsRoute
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val detailsFeatureModule = module {
    single<NavEntryBuilder>(named("feature/details")) {
        { navigator ->
            composable<DetailsRoute> {
                DetailsScreen(
                    navigateBack = {
                        navigator.navigateBack()
                    }
                )
            }
        }
    }
    viewModelOf(::DetailsViewModel)
}