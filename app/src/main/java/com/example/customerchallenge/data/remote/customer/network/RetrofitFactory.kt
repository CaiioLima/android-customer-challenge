package com.example.customerchallenge.data.remote.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object RetrofitFactory {

    fun create(
        baseUrl: String,
        okHttpClient: OkHttpClient
    ): Retrofit {
        require(baseUrl.isNotBlank()) {
            "Base URL must not be blank."
        }

        require(baseUrl.endsWith("/")) {
            "Base URL must end with '/'."
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}