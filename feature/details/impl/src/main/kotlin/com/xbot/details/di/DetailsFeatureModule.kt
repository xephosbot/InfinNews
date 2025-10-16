package com.xbot.details.di

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.xbot.common.navigation.NavEntryBuilder
import com.xbot.designsystem.utils.LocalAnimatedContentScope
import com.xbot.details.DetailsScreen
import com.xbot.details.DetailsViewModel
import com.xbot.details.navigation.DetailsRoute
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val detailsFeatureModule = module {
    single<NavEntryBuilder>(named("feature/details")) {
        {
                navigator ->
            composable<DetailsRoute> { backStackEntry ->
                CompositionLocalProvider(LocalAnimatedContentScope provides this) {
                    val viewModel = koinViewModel<DetailsViewModel> {
                        parametersOf(backStackEntry.toRoute<DetailsRoute>())
                    }
                    DetailsScreen(
                        viewModel = viewModel,
                        onClickBack = {
                            navigator.navigateBack()
                        }
                    )
                }
            }
        }
    }
    viewModelOf(::DetailsViewModel)
}
