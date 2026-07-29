package com.example.customerchallenge.di

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.customerchallenge.data.remote.customer.websocket.WebSocketLifecycleObserver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val koinApplication = startKoin {
            androidContext(this@App)
            modules(appModule, netWorkModule)
        }

        val lifecycleObserver = koinApplication.koin.get<WebSocketLifecycleObserver>()

        ProcessLifecycleOwner
            .get()
            .lifecycle
            .addObserver(lifecycleObserver)
    }
}