package com.cfox.clinea

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
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

    private var textView : TextView ? = null
    private var imageView : ImageView ? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_websocket_main)

        textView = findViewById(R.id.tv_msg)

        imageView = findViewById(R.id.iv_image)
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

                message?.let {
                    val bitmap = BitmapUtils.bytes2Bitmap(it.array())
                    bitmap?.let {
                        imageView?.setImageBitmap(it)
                    }
                }
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
                bytes?.let {
                    val bitmap = BitmapUtils.bytes2Bitmap(it.array())
                    bitmap?.let {
                        imageView?.setImageBitmap(it)
                    }
                }
            }

        })
    }

    fun pushMessage(view: View) {
        clineSocketManager.sendData("this is cline message  : ${System.currentTimeMillis()}")
    }

    fun pushServerMessage(view: View) {
        val pushMsg = "this is server message : ${System.currentTimeMillis()}"
        serverSocketManager.getSocketKeySet().forEach {
            Log.d(TAG, "setListener: addr: $it")
            serverSocketManager.sendData(it, pushMsg)
        }
    }

    fun pushImage(view: View) {

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.niu)
        val byteArray = BitmapUtils.bitmap2Bytes(bitmap)
        clineSocketManager.sendData(byteArray)
    }

    fun serverPushImage(view: View) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.niu)
        val byteArray = BitmapUtils.bitmap2Bytes(bitmap)

        serverSocketManager.getSocketKeySet().forEach {
            serverSocketManager.sendData(it, byteArray)
        }
    }
}