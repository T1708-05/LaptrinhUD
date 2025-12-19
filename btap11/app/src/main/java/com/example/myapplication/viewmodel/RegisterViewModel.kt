package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    
    private val _registerResult = MutableLiveData<RegisterResult>()
    val registerResult: LiveData<RegisterResult> = _registerResult
    
    fun register() {
        val user = username.value?.trim()
        val mail = email.value?.trim()
        val pass = password.value?.trim()
        val confirmPass = confirmPassword.value?.trim()
        
        when {
            user.isNullOrEmpty() || mail.isNullOrEmpty() || pass.isNullOrEmpty() -> {
                _registerResult.value = RegisterResult.Error("Vui lòng nhập đầy đủ thông tin")
                return
            }
            pass != confirmPass -> {
                _registerResult.value = RegisterResult.Error("Mật khẩu xác nhận không khớp")
                return
            }
            pass.length < 6 -> {
                _registerResult.value = RegisterResult.Error("Mật khẩu phải có ít nhất 6 ký tự")
                return
            }
        }
        
        viewModelScope.launch {
            try {
                if (repository.checkUserExists(user)) {
                    _registerResult.value = RegisterResult.Error("Tên đăng nhập đã tồn tại")
                } else {
                    val newUser = User(username = user, email = mail, password = pass)
                    repository.registerUser(newUser)
                    _registerResult.value = RegisterResult.Success
                }
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Lỗi: ${e.message}")
            }
        }
    }
    
    sealed class RegisterResult {
        object Success : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }
}
