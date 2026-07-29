package com.example.customerchallenge.data.remote.customer.error

internal inline fun <T> Result<T>.mapException(
    transform: (Throwable) -> Throwable
): Result<T> {
    return fold(
        onSuccess = { value ->
            Result.success(value)
        },
        onFailure = { throwable ->
            Result.failure(transform(throwable))
        }
    )
}