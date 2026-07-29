package com.example.customerchallenge.data.remote.error

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal object NetworkErrorMapper {

    fun map(throwable: Throwable): NetworkException {
        return when (throwable) {
            is NetworkException -> throwable

            is SocketTimeoutException -> {
                NetworkException.Timeout(cause = throwable)
            }

            is UnknownHostException -> {
                NetworkException.NoConnection(cause = throwable)
            }

            is HttpException -> {
                mapHttpException(throwable)
            }

            is IOException -> {
                NetworkException.NoConnection(cause = throwable)
            }

            else -> {
                NetworkException.Unexpected(cause = throwable)
            }
        }
    }

    private fun mapHttpException(
        exception: HttpException
    ): NetworkException {
        return when (exception.code()) {
            401, 403 -> NetworkException.Unauthorized(
                cause = exception
            )

            404 -> NetworkException.NotFound(
                cause = exception
            )

            in 500..599 -> NetworkException.ServerError(
                statusCode = exception.code(),
                cause = exception
            )

            else -> NetworkException.Unexpected(
                cause = exception
            )
        }
    }
}