package com.example.btap7_2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LightControlState(
    val isConnected: Boolean = false,
    val isLightOn: Boolean = false,
    val message: String = "",
    val serverAddress: String = "192.168.1.100",
    val serverPort: String = "8888"
)

class LightControlViewModel : ViewModel() {
    private val _state = MutableStateFlow(LightControlState())
    val state: StateFlow<LightControlState> = _state.asStateFlow()
    
    private var socketClient: SocketClient? = null
    
    fun updateServerAddress(address: String) {
        _state.value = _state.value.copy(serverAddress = address)
    }
    
    fun updateServerPort(port: String) {
        _state.value = _state.value.copy(serverPort = port)
    }
    
    fun connect() {
        viewModelScope.launch {
            try {
                val port = _state.value.serverPort.toIntOrNull() ?: 8888
                socketClient = SocketClient(_state.value.serverAddress, port)
                
                val result = socketClient?.connect()
                result?.onSuccess { msg ->
                    _state.value = _state.value.copy(
                        isConnected = true,
                        message = msg
                    )
                }?.onFailure { error ->
                    _state.value = _state.value.copy(
                        isConnected = false,
                        message = "Lỗi kết nối: ${error.message}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isConnected = false,
                    message = "Lỗi: ${e.message}"
                )
            }
        }
    }
    
    fun disconnect() {
        viewModelScope.launch {
            socketClient?.disconnect()
            socketClient = null
            _state.value = _state.value.copy(
                isConnected = false,
                isLightOn = false,
                message = "Đã ngắt kết nối"
            )
        }
    }
    
    fun toggleLight() {
        viewModelScope.launch {
            val command = if (_state.value.isLightOn) "OFF" else "ON"
            
            socketClient?.sendCommand(command)?.onSuccess { response ->
                _state.value = _state.value.copy(
                    isLightOn = !_state.value.isLightOn,
                    message = "Phản hồi: $response"
                )
            }?.onFailure { error ->
                _state.value = _state.value.copy(
                    message = "Lỗi gửi lệnh: ${error.message}"
                )
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            socketClient?.disconnect()
        }
    }
}
