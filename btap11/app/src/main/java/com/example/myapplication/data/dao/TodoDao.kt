package com.example.myapplication.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.data.model.Todo

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: Todo)
    
    @Update
    suspend fun updateTodo(todo: Todo)
    
    @Delete
    suspend fun deleteTodo(todo: Todo)
    
    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTodosByUser(userId: Int): LiveData<List<Todo>>
    
    @Query("SELECT * FROM todos WHERE userId = :userId AND isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTodos(userId: Int): LiveData<List<Todo>>
    
    @Query("SELECT * FROM todos WHERE userId = :userId AND isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTodos(userId: Int): LiveData<List<Todo>>
}
