package com.example.customerchallenge.data.remote.customer.websocket

import android.util.Log
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
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

    private var pendingResponse: CompletableDeferred<String>? = null

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

        pendingResponse?.cancel()
        pendingResponse = null

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
                if (text == HELLO_MESSAGE) {
                    pendingResponse?.complete(text)
                }
            }

            override fun onMessage(
                webSocket: WebSocket,
                bytes: ByteString
            ) {
                val message = bytes.utf8()

                Log.d(TAG, "WebSocket binary message received: $message")

                if (message == HELLO_MESSAGE) {
                    pendingResponse?.complete(message)
                }
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
                val responseDeferred = CompletableDeferred<String>()

                pendingResponse?.cancel()
                pendingResponse = responseDeferred

                val wasSent = webSocket.send(HELLO_MESSAGE)

                if (!wasSent) {
                    Log.e(TAG, "Unable to enqueue WebSocket message")

                    pendingResponse = null
                    webSocket.cancel()
                    break
                }

                Log.d(TAG, "WebSocket message sent: $HELLO_MESSAGE")

                val response = withTimeoutOrNull(
                    RESPONSE_TIMEOUT_MILLIS
                ) { responseDeferred.await() }

                pendingResponse = null

                if (response == null) {
                    Log.e(
                        TAG, "WebSocket hello response timed out")

                    webSocket.cancel()
                    break
                }

                Log.d(TAG, "WebSocket hello response confirmed")

                delay(MESSAGE_INTERVAL_MILLIS)
            }
        }
    }

    private fun handleDisconnection() {
        pendingResponse?.cancel()
        pendingResponse = null

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
        val RESPONSE_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10)

        val RECONNECT_DELAY_MILLIS = TimeUnit.SECONDS.toMillis(5)
    }
}