package com.example.boatcontrol

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private var tcpClient: TcpClient? = null
    private val udpClient = UdpClient()
    private var wsClient: WsClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val etHost = findViewById<EditText>(R.id.etHost)
        val etPort = findViewById<EditText>(R.id.etPort)
        val btnConnect = findViewById<Button>(R.id.btnConnect)

        supportFragmentManager.commit {
            replace(R.id.video_container, VideoPlayerFragment().apply { rtspUrl = "rtsp://192.168.1.100:8554/stream" })
        }

        btnConnect.setOnClickListener {
            val host = etHost.text.toString().trim()
            val port = etPort.text.toString().toIntOrNull() ?: 0
            if (host.isEmpty() || port == 0) { Toast.makeText(this, "请输入 Host/Port", Toast.LENGTH_SHORT).show(); return@setOnClickListener }
            launch { connectTcp(host, port) }
        }

        val map = mapOf(
            R.id.btnUp to "forward",
            R.id.btnDown to "back",
            R.id.btnLeft to "left",
            R.id.btnRight to "right"
        )
        map.forEach { idPair ->
            findViewById<Button>(idPair.key).setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { startSending(idPair.value); true }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_OUTSIDE -> { stopSending(); true }
                    else -> false
                }
            }
        }
    }

    private var sendJob: Job? = null
    private fun startSending(cmd: String) {
        stopSending()
        sendJob = launch {
            while (isActive) {
                try { tcpClient?.send(cmd) } catch (e: Exception) {}
                delay(150)
            }
        }
    }
    private fun stopSending() { sendJob?.cancel(); sendJob = null }

    private suspend fun connectTcp(host: String, port: Int) {
        withContext(Dispatchers.IO) {
            try {
                tcpClient?.close()
                tcpClient = TcpClient(host, port)
                tcpClient?.connect(3000)
                withContext(Dispatchers.Main) { Toast.makeText(this@MainActivity, "TCP connected", Toast.LENGTH_SHORT).show() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { Toast.makeText(this@MainActivity, "connect fail: ${e.message}", Toast.LENGTH_LONG).show() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tcpClient?.close()
        udpClient.close()
        wsClient?.close()
        cancel()
    }
}
