package com.example.customerchallenge.di

import com.example.customerchallenge.data.remote.api.CustomerApi
import com.example.customerchallenge.data.remote.network.HttpLoggingInterceptorFactory
import com.example.customerchallenge.data.remote.network.NetworkConstants
import com.example.customerchallenge.data.remote.network.OkHttpClientFactory
import com.example.customerchallenge.data.remote.network.RetrofitFactory
import org.koin.dsl.module

val netWorkModule = module {

    single {
        HttpLoggingInterceptorFactory.create()
    }

    single {
        OkHttpClientFactory.create(
            loggingInterceptor = get()
        )
    }

    single {
        RetrofitFactory.create(
            baseUrl = NetworkConstants.BASE_URL,
            okHttpClient = get()
        )
    }

}