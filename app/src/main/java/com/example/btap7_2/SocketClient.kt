package com.example.btap7_2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class SocketClient(
    private val serverAddress: String,
    private val serverPort: Int
) {
    private var socket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    
    suspend fun connect(): Result<String> = withContext(Dispatchers.IO) {
        try {
            socket = Socket(serverAddress, serverPort)
            writer = PrintWriter(socket!!.getOutputStream(), true)
            reader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            Result.success("Kết nối thành công!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun sendCommand(command: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (socket == null || socket?.isClosed == true) {
                return@withContext Result.failure(Exception("Chưa kết nối đến server"))
            }
            
            writer?.println(command)
            val response = reader?.readLine() ?: "Không có phản hồi"
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            writer?.close()
            reader?.close()
            socket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun isConnected(): Boolean {
        return socket?.isConnected == true && socket?.isClosed == false
    }
}
