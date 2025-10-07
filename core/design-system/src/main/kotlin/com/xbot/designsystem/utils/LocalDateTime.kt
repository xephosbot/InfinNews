package com.xbot.designsystem.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
fun LocalDateTime.toLocalizedString(locale: Locale = Locale.getDefault()): String {
    return format(locale, "d MMM yyyy \u2022 HH:mm")
}

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalDateTime.format(locale: Locale, format: String): String {
    return DateTimeFormatter.ofPattern(format, locale).format(this.toJavaLocalDateTime())
}