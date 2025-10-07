package com.xbot.common.navigation

interface Navigator {
    fun navigate(key: NavKey)
    fun navigateBack()
}