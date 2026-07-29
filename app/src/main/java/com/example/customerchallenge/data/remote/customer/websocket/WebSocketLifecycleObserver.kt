package com.example.customerchallenge.data.remote.websocket

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class WebSocketLifecycleObserver(
    private val webSocketManager: EchoWebSocketManager
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        webSocketManager.connect()
    }

    override fun onStop(owner: LifecycleOwner) {
        webSocketManager.disconnect()
    }
}