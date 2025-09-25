package com.xbot.details.navigation

import com.xbot.common.navigation.NavKey
import com.xbot.common.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val url: String) : NavKey

fun Navigator.navigateToDetails(url: String) {
    navigate(DetailsRoute(url))
}