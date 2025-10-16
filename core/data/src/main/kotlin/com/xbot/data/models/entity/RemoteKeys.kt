package com.xbot.data.models.entity

import androidx.room.Entity
import com.xbot.domain.model.NewsCategory

@Entity(tableName = "remote_keys", primaryKeys = ["articleUrl", "category"])
internal data class RemoteKeys(
    val articleUrl: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val category: NewsCategory
)
