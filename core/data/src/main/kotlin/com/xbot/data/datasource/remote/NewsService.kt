package com.xbot.data.datasource.remote

import arrow.core.Either
import com.xbot.data.models.dto.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NewsService {
    @GET("everything")
    suspend fun getEverything(
        @Query("q") query: String,
        @Query("searchIn") searchIn: String = "title",
        @Query("sources") sources: String? = null,
        @Query("domains") domains: String? = null,
        @Query("excludeDomains") excludeDomains: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("language") language: String = "en",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int? = null,
        @Query("page") page: Int? = null,
    ): Either<Throwable, Response.Success>

    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String? = null,
        @Query("category") category: String? = null,
        @Query("sources") sources: String? = null,
        @Query("q") query: String? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("page") page: Int? = null,
    ): Either<Throwable, Response.Success>
}