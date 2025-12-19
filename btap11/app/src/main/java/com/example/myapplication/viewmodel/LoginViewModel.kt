package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.User
import com.example.myapplication.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    
    val username = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    
    fun login() {
        val user = username.value?.trim()
        val pass = password.value?.trim()
        
        if (user.isNullOrEmpty() || pass.isNullOrEmpty()) {
            _loginResult.value = LoginResult.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }
        
        viewModelScope.launch {
            try {
                val loggedInUser = repository.loginUser(user, pass)
                if (loggedInUser != null) {
                    _loginResult.value = LoginResult.Success(loggedInUser)
                } else {
                    _loginResult.value = LoginResult.Error("Tên đăng nhập hoặc mật khẩu không đúng")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error("Lỗi: ${e.message}")
            }
        }
    }
    
    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}
