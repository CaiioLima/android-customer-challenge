package com.example.customerchallenge.di

import com.example.customerchallenge.data.remote.customer.api.CustomerApi
import com.example.customerchallenge.data.remote.customer.network.HttpLoggingInterceptorFactory
import com.example.customerchallenge.data.remote.customer.network.NetworkConstants
import com.example.customerchallenge.data.remote.customer.network.OkHttpClientFactory
import com.example.customerchallenge.data.remote.customer.network.RetrofitFactory
import com.example.customerchallenge.data.remote.customer.websocket.EchoWebSocketManager
import com.example.customerchallenge.data.remote.customer.websocket.WebSocketLifecycleObserver
import org.koin.dsl.module
import retrofit2.Retrofit

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

    single<CustomerApi> { get<Retrofit>().create(CustomerApi::class.java) }

    single {
        EchoWebSocketManager(
            okHttpClient = get()
        )
    }

    single {
        WebSocketLifecycleObserver(
            webSocketManager = get()
        )
    }

}