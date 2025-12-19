package com.example.myapplication.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.repository.TodoRepository
import com.example.myapplication.databinding.ActivityTodoListBinding
import com.example.myapplication.databinding.DialogAddTodoBinding
import com.example.myapplication.ui.adapter.TodoAdapter
import com.example.myapplication.viewmodel.TodoViewModel
import com.example.myapplication.viewmodel.TodoViewModelFactory

class TodoListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTodoListBinding
    private lateinit var adapter: TodoAdapter
    private val viewModel: TodoViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TodoRepository(database.todoDao())
        TodoViewModelFactory(repository)
    }
    
    private var userId: Int = 0
    private var username: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_list)
        binding.lifecycleOwner = this
        
        userId = intent.getIntExtra("USER_ID", 0)
        username = intent.getStringExtra("USERNAME") ?: ""
        
        binding.tvWelcome.text = "Xin chào, $username!"
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        viewModel.setUserId(userId)
    }
    
    private fun setupRecyclerView() {
        adapter = TodoAdapter(
            onItemClick = { todo ->
                // Có thể mở dialog để edit
            },
            onCheckClick = { todo ->
                viewModel.toggleTodoComplete(todo)
            },
            onDeleteClick = { todo ->
                showDeleteConfirmation(todo)
            }
        )
        
        binding.rvTodos.layoutManager = LinearLayoutManager(this)
        binding.rvTodos.adapter = adapter
    }
    
    private fun setupObservers() {
        viewModel.todos.observe(this) { todos ->
            adapter.submitList(todos)
        }
        
        viewModel.operationResult.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            showAddTodoDialog()
        }
    }
    
    private fun showAddTodoDialog() {
        val dialogBinding = DialogAddTodoBinding.inflate(layoutInflater)
        
        AlertDialog.Builder(this)
            .setTitle("Thêm Công Việc")
            .setView(dialogBinding.root)
            .setPositiveButton("Thêm") { _, _ ->
                val title = dialogBinding.etTitle.text.toString()
                val description = dialogBinding.etDescription.text.toString()
                viewModel.addTodo(title, description)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
    
    private fun showDeleteConfirmation(todo: com.example.myapplication.data.model.Todo) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận")
            .setMessage("Bạn có chắc muốn xóa công việc này?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteTodo(todo)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
