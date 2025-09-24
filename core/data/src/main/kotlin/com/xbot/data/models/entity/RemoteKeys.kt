package com.xbot.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val articleUrl: String,
    val prevKey: Int?,
    val nextKey: Int?,
    val category: String
)