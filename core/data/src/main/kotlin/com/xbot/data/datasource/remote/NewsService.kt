package com.xbot.data.datasource.remote

import com.xbot.core.data.BuildConfig
import com.xbot.data.models.dto.Response
import com.xbot.domain.model.NewsCategory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

internal interface NewsService {
    @Headers("X-Api-Key: ${BuildConfig.API_KEY}")
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: NewsCategory,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
    ): Response.Success
}
