package com.xbot.domain

sealed class Error(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    class NetworkError(cause: Throwable?) : Error("Internet not available", cause)
    class SerializationError(message: String?) : Error(message)
    class IOError(cause: Throwable?) : Error(cause = cause)
    class HttpError(code: Int, message: String?) : Error("HTTP $code: $message")
    class Unknown(cause: Throwable?) : Error("Unknown error", cause)
}