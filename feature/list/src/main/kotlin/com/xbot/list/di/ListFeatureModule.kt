package com.xbot.list.di

import com.xbot.list.ListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val listFeatureModule = module {
    viewModelOf(::ListViewModel)
}