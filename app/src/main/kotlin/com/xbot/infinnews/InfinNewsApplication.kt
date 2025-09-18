package com.xbot.infinnews

import android.app.Application
import com.xbot.data.di.dataModule
import com.xbot.list.di.listFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InfinNewsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@InfinNewsApplication)
            androidLogger()
            modules(dataModule + listFeatureModule)
        }
    }
}