package com.example.myapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Todo
import com.example.myapplication.data.repository.TodoRepository
import kotlinx.coroutines.launch

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {
    
    private var currentUserId: Int = 0
    
    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos
    
    private val _operationResult = MutableLiveData<String>()
    val operationResult: LiveData<String> = _operationResult
    
    fun setUserId(userId: Int) {
        currentUserId = userId
        loadTodos()
    }
    
    private fun loadTodos() {
        repository.getTodosByUser(currentUserId).observeForever { todoList ->
            _todos.value = todoList
        }
    }
    
    fun addTodo(title: String, description: String) {
        if (title.isBlank()) {
            _operationResult.value = "Tiêu đề không được để trống"
            return
        }
        
        viewModelScope.launch {
            try {
                val todo = Todo(
                    userId = currentUserId,
                    title = title,
                    description = description
                )
                repository.insertTodo(todo)
                _operationResult.value = "Thêm công việc thành công"
            } catch (e: Exception) {
                _operationResult.value = "Lỗi: ${e.message}"
            }
        }
    }
    
    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                repository.updateTodo(todo)
                _operationResult.value = "Cập nhật thành công"
            } catch (e: Exception) {
                _operationResult.value = "Lỗi: ${e.message}"
            }
        }
    }
    
    fun toggleTodoComplete(todo: Todo) {
        viewModelScope.launch {
            try {
                val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
                repository.updateTodo(updatedTodo)
            } catch (e: Exception) {
                _operationResult.value = "Lỗi: ${e.message}"
            }
        }
    }
    
    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            try {
                repository.deleteTodo(todo)
                _operationResult.value = "Xóa thành công"
            } catch (e: Exception) {
                _operationResult.value = "Lỗi: ${e.message}"
            }
        }
    }
}
