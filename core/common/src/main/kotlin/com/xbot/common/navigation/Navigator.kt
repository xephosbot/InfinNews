package com.xbot.common.navigation

interface Navigator <T : NavKey>{
    fun navigate(key: T)
    fun navigateBack()
}