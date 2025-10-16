package com.xbot.data.utils

import okhttp3.OkHttpClient

internal fun OkHttpClient.Builder.conditional(
    condition: Boolean,
    action: OkHttpClient.Builder.() -> OkHttpClient.Builder
): OkHttpClient.Builder {
    return if (condition) {
        this.action()
    } else {
        this
    }
}
