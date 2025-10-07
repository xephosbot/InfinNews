package com.xbot.details.navigation

import com.xbot.common.navigation.NavKey
import com.xbot.common.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val url: String, val category: String) : NavKey

fun Navigator.navigateToDetails(url: String, category: String) {
    navigate(DetailsRoute(url, category))
}