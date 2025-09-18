package com.xbot.data.utils

import arrow.core.Either
import com.xbot.data.models.dto.Response
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.awaitResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class EitherCallAdapter<R : Any>(
    private val successType: Type
) : CallAdapter<R, Any> {

    override fun responseType(): Type = successType

    override fun adapt(call: Call<R?>): Any {
        return suspend {
            try {
                val body = call.awaitResponse().body()
                when (body) {
                    is Response.Success -> Either.Right(body)
                    is Response.Error -> Either.Left(Exception("Api error: ${body.code} ${body.message}"))
                    null -> Either.Left(NullPointerException("Response body is null"))
                    else -> Either.Left(IllegalStateException("Unexpected response type: $body"))
                }
            } catch (t: Throwable) {
                Either.Left(t)
            }
        }
    }
}

internal class EitherCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Either::class.java) return null

        val parameterizedType = returnType as? ParameterizedType
            ?: throw IllegalStateException("Either must be parameterized")

        val rightType = parameterizedType.actualTypeArguments[1]
        return EitherCallAdapter<Response>(rightType)
    }
}