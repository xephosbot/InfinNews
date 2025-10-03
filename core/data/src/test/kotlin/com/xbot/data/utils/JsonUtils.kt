package com.xbot.data.utils

import kotlinx.serialization.json.Json
import okio.buffer
import okio.source
import org.koin.test.KoinTest
import org.koin.test.get

object JsonUtils : KoinTest {
    internal inline fun <reified T> toJson(value: T): String {
        return get<Json>().encodeToString(value)
    }

    internal inline fun <reified T> fromJson(json: String): T {
        return get<Json>().decodeFromString(json)
    }

    internal fun readJsonFile(fileName: String): String {
        val inputStream = javaClass.classLoader!!.getResourceAsStream(fileName)
        return inputStream.source().buffer().readString(Charsets.UTF_8)
    }
}