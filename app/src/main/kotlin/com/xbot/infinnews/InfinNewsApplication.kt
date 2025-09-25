package com.xbot.infinnews

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import com.xbot.data.di.dataModule
import com.xbot.details.di.detailsFeatureModule
import com.xbot.list.di.listFeatureModule
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class InfinNewsApplication : Application(), SingletonImageLoader.Factory {
    private val okHttpClient by inject<OkHttpClient>()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@InfinNewsApplication)
            androidLogger()
            modules(dataModule + listFeatureModule + detailsFeatureModule)
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .components {
                add(
                    OkHttpNetworkFetcherFactory(
                        callFactory = { okHttpClient }
                    )
                )
            }
            .build()
    }
}