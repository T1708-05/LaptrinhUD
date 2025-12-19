package com.example.myapplication.data.repository

import androidx.lifecycle.LiveData
import com.example.myapplication.data.dao.TodoDao
import com.example.myapplication.data.model.Todo

class TodoRepository(private val todoDao: TodoDao) {
    
    fun getTodosByUser(userId: Int): LiveData<List<Todo>> {
        return todoDao.getTodosByUser(userId)
    }
    
    fun getActiveTodos(userId: Int): LiveData<List<Todo>> {
        return todoDao.getActiveTodos(userId)
    }
    
    fun getCompletedTodos(userId: Int): LiveData<List<Todo>> {
        return todoDao.getCompletedTodos(userId)
    }
    
    suspend fun insertTodo(todo: Todo) {
        todoDao.insertTodo(todo)
    }
    
    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }
    
    suspend fun deleteTodo(todo: Todo) {
        todoDao.deleteTodo(todo)
    }
}
