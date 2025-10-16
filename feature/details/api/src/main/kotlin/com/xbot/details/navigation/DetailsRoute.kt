package com.xbot.details.navigation

import com.xbot.common.navigation.NavKey
import com.xbot.common.navigation.Navigator
import kotlinx.serialization.Serializable

@Serializable
data class DetailsRoute(val url: String, val id: String) : NavKey

fun Navigator.navigateToDetails(url: String, id: String) {
    navigate(DetailsRoute(url, id))
}
