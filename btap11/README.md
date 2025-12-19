# 📱 ToDoList App - Kiến Trúc MVVM

Ứng dụng quản lý công việc (ToDoList) được xây dựng với kiến trúc **MVVM (Model-View-ViewModel)** sử dụng Kotlin, Room Database, DataBinding và LiveData.

## 📸 Screenshots

<div align="center">
  <img src="img/login.png" width="250" alt="Màn hình đăng nhập"/>
  <img src="img/register.png" width="250" alt="Màn hình đăng ký"/>
  <img src="img/todolist.png" width="250" alt="Danh sách công việc"/>
</div>

<div align="center">
  <img src="img/add_todo.png" width="250" alt="Thêm công việc"/>
  <img src="img/cautrucfile.png" width="250" alt="Công việc hoàn thành"/>
</div>


## 🏗️ Kiến Trúc MVVM

```
┌─────────────────────────────────────────┐
│              VIEW LAYER                 │
│  (Activities, XML Layouts, DataBinding)│
└──────────────┬──────────────────────────┘
               │ observe LiveData
               ▼
┌─────────────────────────────────────────┐
│           VIEWMODEL LAYER               │
│  (Business Logic, State Management)     │
└──────────────┬──────────────────────────┘
               │ call repository
               ▼
┌─────────────────────────────────────────┐
│            MODEL LAYER                  │
│  (Data, Repository, Database)           │
└─────────────────────────────────────────┘
```


