package com.xbot.data.models.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SourceDto(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
)
