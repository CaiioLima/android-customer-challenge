package com.example.customerchallenge.data.remote.websocket

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.TimeUnit

class EchoWebSocketManager(
    private val okHttpClient: OkHttpClient
) {

    private val managerScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var webSocket: WebSocket? = null
    private var messageJob: Job? = null
    private var reconnectJob: Job? = null

    @Volatile
    private var shouldRemainConnected = false

    fun connect() {
        if (
            shouldRemainConnected &&
            webSocket != null
        ) {
            return
        }

        shouldRemainConnected = true
        openConnection()
    }

    fun disconnect() {
        shouldRemainConnected = false

        messageJob?.cancel()
        messageJob = null

        reconnectJob?.cancel()
        reconnectJob = null

        webSocket?.close(
            NORMAL_CLOSURE_STATUS,
            NORMAL_CLOSURE_REASON
        )

        webSocket = null
    }

    private fun openConnection() {
        if (webSocket != null) return

        val request = Request.Builder()
            .url(WEB_SOCKET_URL)
            .build()

        webSocket = okHttpClient.newWebSocket(
            request = request,
            listener = createWebSocketListener()
        )
    }

    private fun createWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {

            override fun onOpen(
                webSocket: WebSocket,
                response: Response
            ) {
                Log.d(TAG, "WebSocket connection opened")
                reconnectJob?.cancel()
                reconnectJob = null
                startMessageLoop(webSocket)
            }

            override fun onMessage(
                webSocket: WebSocket,
                text: String
            ) {
                Log.d(TAG, "WebSocket message received: $text")
            }

            override fun onMessage(
                webSocket: WebSocket,
                bytes: ByteString
            ) {
                val message = bytes.utf8()

                Log.d(TAG, "WebSocket binary message received: $message")
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.d(TAG, "WebSocket connection is closing. Code: $code, reason: $reason")
                webSocket.close(code, reason)
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                Log.d(TAG, "WebSocket connection closed. Code: $code, reason: $reason")
                handleDisconnection()
            }

            override fun onFailure(
                webSocket: WebSocket,
                throwable: Throwable,
                response: Response?
            ) {
                Log.e(TAG, "WebSocket connection failed", throwable)
                handleDisconnection()
            }
        }
    }

    private fun startMessageLoop(
        webSocket: WebSocket
    ) {
        messageJob?.cancel()
        messageJob = managerScope.launch {
            while (isActive && shouldRemainConnected) {
                val wasSent = webSocket.send(HELLO_MESSAGE)
                if (wasSent) {
                    Log.d(TAG, "WebSocket message sent: $HELLO_MESSAGE")
                } else {
                    Log.e(TAG, "Unable to enqueue WebSocket message")
                }
                delay(MESSAGE_INTERVAL_MILLIS)
            }
        }
    }

    private fun handleDisconnection() {
        webSocket = null

        messageJob?.cancel()
        messageJob = null

        scheduleReconnect()
    }

    private fun scheduleReconnect() {
        if (!shouldRemainConnected) return
        if (reconnectJob?.isActive == true) return

        reconnectJob = managerScope.launch {
            delay(RECONNECT_DELAY_MILLIS)

            if (shouldRemainConnected) {
                Log.d(TAG, "Attempting to reconnect WebSocket")

                openConnection()
            }
        }
    }

    private companion object {
        const val TAG = "EchoWebSocketManager"

        const val WEB_SOCKET_URL = "wss://ws.postman-echo.com/raw"
        const val HELLO_MESSAGE = "hello"

        const val NORMAL_CLOSURE_STATUS = 1000
        const val NORMAL_CLOSURE_REASON = "Application moved to background"

        val MESSAGE_INTERVAL_MILLIS = TimeUnit.SECONDS.toMillis(30)

        val RECONNECT_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(5)
    }
}