package com.xbot.designsystem.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Instant.toLocalizedString(locale: Locale = Locale.getDefault()): String {
    return toLocalDateTime(TimeZone.currentSystemDefault()).toLocalizedString(locale)
}

fun LocalDateTime.toLocalizedString(locale: Locale = Locale.getDefault()): String {
    return format(locale, "d MMM yyyy \u2022 HH:mm")
}

private fun LocalDateTime.format(locale: Locale, format: String): String {
    return DateTimeFormatter.ofPattern(format, locale).format(this.toJavaLocalDateTime())
}