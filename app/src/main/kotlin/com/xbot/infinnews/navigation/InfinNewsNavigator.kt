package com.xbot.infinnews.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.xbot.common.navigation.NavKey
import com.xbot.common.navigation.Navigator

@Composable
internal fun rememberInfinNewsNavigator(
    navController: NavHostController = rememberNavController()
): InfinNewsNavigator {
    return remember(navController) { InfinNewsNavigator(navController) }
}

internal class InfinNewsNavigator(
    val navController: NavHostController
) : Navigator {
    override fun navigate(key: NavKey) {
        navController.navigate(key)
    }

    override fun navigateBack() {
        navController.navigateUp()
    }
}