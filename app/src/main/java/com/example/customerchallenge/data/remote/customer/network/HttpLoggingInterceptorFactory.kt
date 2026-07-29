package com.example.customerchallenge.data.remote.customer.network

import okhttp3.logging.HttpLoggingInterceptor
import com.example.customerchallenge.BuildConfig

internal object HttpLoggingInterceptorFactory {

    fun create(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
}