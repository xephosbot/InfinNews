package com.xbot.common.navigation

interface Navigator <T : NavKey>{
    val backstack: List<T>
    val currentDestination: T?
    fun navigate(key: T)
    fun navigateBack()
}