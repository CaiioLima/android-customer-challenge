package com.example.customerchallenge.di

import com.example.customerchallenge.data.remote.network.HttpLoggingInterceptorFactory
import com.example.customerchallenge.data.remote.network.OkHttpClientFactory
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

}