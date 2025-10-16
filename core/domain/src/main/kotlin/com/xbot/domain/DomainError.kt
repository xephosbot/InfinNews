package com.xbot.domain

sealed class DomainError(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    class NetworkError(cause: Throwable?) : DomainError("Internet not available", cause)
    class SerializationError(message: String?) : DomainError(message)
    class IOError(cause: Throwable?) : DomainError(cause = cause)
    class HttpError(code: Int, message: String?) : DomainError("HTTP $code: $message")
    class Unknown(cause: Throwable?) : DomainError("Unknown error", cause)
}
