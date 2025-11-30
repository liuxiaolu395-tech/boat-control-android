package com.example.boatcontrol

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.net.InetSocketAddress
import java.net.Socket

class TcpClient(private val host: String, private val port: Int) {
    private var socket: Socket? = null
    private var writer: BufferedWriter? = null

    suspend fun connect(timeoutMs: Int = 3000) = withContext(Dispatchers.IO) {
        if (socket?.isConnected == true) return@withContext
        close()
        val s = Socket()
        s.connect(InetSocketAddress(host, port), timeoutMs)
        socket = s
        writer = BufferedWriter(OutputStreamWriter(s.getOutputStream(), Charsets.UTF_8))
    }

    suspend fun send(text: String) = withContext(Dispatchers.IO) {
        if (writer == null) connect()
        writer?.apply {
            write(text)
            newLine()
            flush()
        }
    }

    fun close() {
        try { writer?.close() } catch (_: Exception) {}
        try { socket?.close() } catch (_: Exception) {}
        writer = null
        socket = null
    }

    fun isConnected(): Boolean = socket?.isConnected == true && socket?.isClosed == false
}
