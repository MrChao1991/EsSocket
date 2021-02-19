package com.cfox.clinea

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.cfox.essocket.ClineSocketHelper
import com.cfox.essocket.ServerSocketHelper
import com.cfox.essocket.SocketManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val serverSocketHelper  = ServerSocketHelper()
    private val clineSocketHelper = ClineSocketHelper()

    private var clineSocketManager : SocketManager? = null

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
        serverHandler.post {
            serverSocketHelper.startServer(serverPort)
        }
    }

    fun startSocket(view: View) {
        val host = findViewById<EditText>(R.id.et_host).text.toString()
        val port = findViewById<EditText>(R.id.et_host_port).text.toString().toInt()

        clineHandler.post {
            val socket = clineSocketHelper.connectSocket(host, port)
            socket?.apply {
                clineSocketManager = SocketManager(this)
            }
        }
    }

    fun pushMessage(view: View) {
        clineSocketManager?.pushByte("this is cline message".toByteArray())
    }

    fun pushServerMessage(view: View) {
        val pushMsg = "this is server message"
        serverSocketHelper.getSocketKeySet().forEach {
            Log.d(TAG, "setListener: addr: $it")
            val socket = serverSocketHelper.getSocket(it)
            socket?.pushByte(pushMsg.toByteArray())
        }
    }

    fun setListener(view: View) {
        clineHandler.post {
            clineSocketManager?.setListener(object : SocketManager.SocketMessageListener {
                override fun onCallback(byte: ByteArray) {
                    Log.d(TAG, "onCallback: cline b get msg: $byte")
                    textView?.post {
                        textView?.text = byte.toString()
                    }
                }
            })

            clineSocketManager?.startReadSocket()

            serverSocketHelper.getSocketKeySet().forEach {
                Log.d(TAG, "setListener: addr: $it")
                val socket = serverSocketHelper.getSocket(it)
                socket?.setListener(object : SocketManager.SocketMessageListener {
                    override fun onCallback(byte: ByteArray) {
                        Log.d(TAG, "onCallback: cline b server addr:$it  msg:$byte")
                        textView?.post {
                            textView?.text = byte.toString()
                        }
                    }
                })
                serverSocketHelper.getSocket(it)?.startReadSocket()
            }
        }
    }
}