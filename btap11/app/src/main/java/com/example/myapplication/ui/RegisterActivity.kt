package com.example.myapplication.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.repository.UserRepository
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.viewmodel.RegisterViewModel
import com.example.myapplication.viewmodel.UserViewModelFactory

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())
        UserViewModelFactory(repository)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        
        setupObservers()
        setupListeners()
    }
    
    private fun setupObservers() {
        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is RegisterViewModel.RegisterResult.Success -> {
                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is RegisterViewModel.RegisterResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            viewModel.register()
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
}
