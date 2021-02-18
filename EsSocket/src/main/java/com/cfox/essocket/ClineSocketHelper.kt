package com.cfox.essocket

import java.net.Socket

class ClineSocketHelper {

    companion object {
        private const val TAG = "ClineSocketHelper"
    }

    private var socket : Socket ? = null

    fun connectSocket(host: String, port: Int) {
        try {
            socket = Socket(host, port)

        } catch (e: Exception) {

        }

    }


}