package com.xbot.data.utils

import androidx.room.TypeConverter
import com.xbot.domain.model.NewsCategory
import kotlinx.datetime.Instant

object NewsCategoryConverter {
    @TypeConverter
    fun fromString(value: String): NewsCategory {
        return NewsCategory.fromString(value)
    }

    @TypeConverter
    fun toString(value: NewsCategory): String {
        return value.toString()
    }
}

object InstantConverter {
    @TypeConverter
    fun fromLong(value: Long): Instant {
        return Instant.fromEpochMilliseconds(value)
    }

    @TypeConverter
    fun toLong(value: Instant): Long {
        return value.toEpochMilliseconds()
    }
}
