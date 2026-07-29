package com.example.customerchallenge.data.remote.network

import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig

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