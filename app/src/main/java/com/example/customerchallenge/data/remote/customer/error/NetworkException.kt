package com.example.customerchallenge.data.remote.error

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    class NoConnection(
        cause: Throwable? = null
    ) : NetworkException(
        message = "No internet connection",
        cause = cause
    )

    class Timeout(
        cause: Throwable? = null
    ) : NetworkException(
        message = "The request timed out",
        cause = cause
    )

    class Unauthorized(
        cause: Throwable? = null
    ) : NetworkException(
        message = "Unauthorized request",
        cause = cause
    )

    class NotFound(
        cause: Throwable? = null
    ) : NetworkException(
        message = "Resource not found",
        cause = cause
    )

    class ServerError(
        val statusCode: Int,
        cause: Throwable? = null
    ) : NetworkException(
        message = "Server error: $statusCode",
        cause = cause
    )

    class Unexpected(
        cause: Throwable? = null
    ) : NetworkException(
        message = cause?.message ?: "Unexpected network error",
        cause = cause
    )
}