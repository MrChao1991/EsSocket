package com.cfox.essocket

import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class ServerSocketHelper {
    companion object {
        private const val TAG = "ServerSocketHelper"
    }

    private val socketMap = mutableMapOf<String, Socket>()
    private var serverSocket : ServerSocket ? = null
    private var serverStarting = false

    fun startServer(port: Int) {
        Log.d(TAG, "startServer: ..... start ....")
        try {
            serverStarting = true
            serverSocket = ServerSocket(port)
            while (serverStarting) {
                val socket = serverSocket?.accept()
                socket?.let {
                    val host = it.inetAddress.hostAddress
                    val port = it.port
                    Log.d(TAG, "startServer: cline : host:$host  port:$port")
                    val key = "$host:$port"
                    socketMap[key] = socket
                }
            }
        } catch (e: IOException) {

        }
        Log.d(TAG, "startServer: ..... end ....")
    }

    fun closeServer() {
        Log.d(TAG, "closeServer:  start ....")
        serverStarting = false
        socketMap.forEach{
            if (!it.value.isClosed) {
                it.value.close()
            }
        }

        serverSocket?.let {
            if (!it.isClosed) {
                it.close()
            }
        }
        Log.d(TAG, "closeServer: ... end ....")
    }

    fun closeSocket(host: String, port: Int) {
        val key = "$host:$port"
        val socket = socketMap[key]
        socket?.let {
            if (!it.isClosed) {
                it.close()
            }
        }
        socketMap.remove(key)
    }

    fun getSocketKeySet() : Set<String> {
        return socketMap.keys
    }

    fun getSocket(key : String) : Socket? {
        return socketMap[key]
    }
}