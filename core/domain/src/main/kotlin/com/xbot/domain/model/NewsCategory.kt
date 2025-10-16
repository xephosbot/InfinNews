package com.xbot.domain.model

enum class NewsCategory(private val value: String) {
    GENERAL("general"),
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    override fun toString(): String = value

    companion object {
        fun fromString(value: String): NewsCategory {
            return entries.firstOrNull { it.value == value } ?: GENERAL
        }
    }
}
