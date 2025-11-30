package com.example.boatcontrol

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpClient {
    private var socket: DatagramSocket? = null

    suspend fun send(host: String, port: Int, text: String) = withContext(Dispatchers.IO) {
        try {
            if (socket == null) socket = DatagramSocket()
            val buf = text.toByteArray(Charsets.UTF_8)
            val packet = DatagramPacket(buf, buf.size, InetAddress.getByName(host), port)
            socket!!.send(packet)
        } catch (e: Exception) { /* ignore for sample */ }
    }

    fun close() {
        socket?.close()
        socket = null
    }
}
