package com.cfox.clinea

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.cfox.essocket.SocketManager
import com.cfox.essocket.web.SocketClineListener
import com.cfox.essocket.web.SocketServerListener
import com.cfox.essocket.web.WebSocketClineManager
import com.cfox.essocket.web.WebSocketServerManager
import java.nio.ByteBuffer

class MainWebSocketActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val serverSocketManager  = WebSocketServerManager()
    private val clineSocketManager = WebSocketClineManager()

    private val serverThread = HandlerThread("t-server")
    private val clineThread = HandlerThread("t-cline")

    private var serverHandler : Handler
    private var clineHandler : Handler
    init {
        serverThread.start()
        serverHandler = Handler(serverThread.looper)

        clineThread.start()
        clineHandler = Handler(clineThread.looper)
    }

    private var textView : TextView ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById<TextView>(R.id.tv_msg)
    }

    fun startServer(view: View) {
        val serverPort = findViewById<EditText>(R.id.et_server_port).text.toString().toInt()
        serverSocketManager.startServer(serverPort, object : SocketServerListener {
            override fun onMessage(message: String?) {
                textView?.post {
                    textView?.text = message
                }
            }

            override fun onMessage(message: ByteBuffer?) {

            }

        })

    }

    fun startSocket(view: View) {
        val host = findViewById<EditText>(R.id.et_host).text.toString()
        val port = findViewById<EditText>(R.id.et_host_port).text.toString().toInt()

        clineSocketManager.connectServer(host, port, object : SocketClineListener{
            override fun onMessage(message: String?) {
                textView?.post {
                    textView?.text = message
                }
            }

            override fun onMessage(bytes: ByteBuffer?) {

            }

        })
    }

    fun pushMessage(view: View) {
        clineSocketManager.sendData("this is cline message")
    }

    fun pushServerMessage(view: View) {
        val pushMsg = "this is server message"
        serverSocketManager.getSocketKeySet().forEach {
            Log.d(TAG, "setListener: addr: $it")
            serverSocketManager.sendData(it, pushMsg)
        }
    }

    fun pushImage(view: View) {


    }

    fun setListener(view: View) {

    }
}