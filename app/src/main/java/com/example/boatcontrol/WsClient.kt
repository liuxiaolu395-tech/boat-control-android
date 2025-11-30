package com.example.boatcontrol

import kotlinx.coroutines.channels.Channel
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class WsClient(private val url: String) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()
    private var ws: WebSocket? = null
    val incoming = Channel<String>(Channel.UNLIMITED)

    fun connect() {
        val req = Request.Builder().url(url).build()
        ws = client.newWebSocket(req, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {}
            override fun onMessage(webSocket: WebSocket, text: String) { incoming.trySend(text) }
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) { incoming.trySend(bytes.utf8()) }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {}
        })
    }

    fun send(msg: String) { ws?.send(msg) }
    fun close() { ws?.close(1000, "bye"); client.dispatcher.executorService.shutdown() }
}
