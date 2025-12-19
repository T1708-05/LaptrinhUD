package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.UserDao
import com.example.myapplication.data.model.User

class UserRepository(private val userDao: UserDao) {
    
    suspend fun registerUser(user: User): Long {
        return userDao.insertUser(user)
    }
    
    suspend fun loginUser(username: String, password: String): User? {
        return userDao.login(username, password)
    }
    
    suspend fun checkUserExists(username: String): Boolean {
        return userDao.getUserByUsername(username) != null
    }
}
