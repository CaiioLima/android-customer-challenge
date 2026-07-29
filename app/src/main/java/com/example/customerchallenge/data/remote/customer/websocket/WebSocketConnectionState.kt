package com.example.customerchallenge.data.remote.customer.websocket

sealed interface WebSocketConnectionState {

    data object Disconnected : WebSocketConnectionState

    data object Connecting : WebSocketConnectionState

    data object Connected : WebSocketConnectionState

    data class MessageReceived(
        val message: String
    ) : WebSocketConnectionState

    data class Error(
        val throwable: Throwable
    ) : WebSocketConnectionState
}