package com.xbot.data.datasource.remote

import com.xbot.data.BuildConfig
import com.xbot.data.models.dto.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

internal interface NewsService {
    @Headers("X-Api-Key: ${BuildConfig.API_KEY}")
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
    ): Response
}