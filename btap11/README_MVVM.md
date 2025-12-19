# Ứng Dụng ToDoList với Kiến Trúc MVVM

## Giới Thiệu Mô Hình MVVM

MVVM (Model-View-ViewModel) là một mô hình kiến trúc phần mềm giúp tách biệt logic nghiệp vụ khỏi giao diện người dùng.

### Các Thành Phần Chính:

#### 1. **Model** (Mô hình dữ liệu)
- Đại diện cho dữ liệu và logic nghiệp vụ
- Trong dự án này:
  - `User.kt`: Entity cho người dùng
  - `Todo.kt`: Entity cho công việc
  - `UserDao.kt`, `TodoDao.kt`: Truy cập dữ liệu từ SQLite
  - `AppDatabase.kt`: Room Database
  - `UserRepository.kt`, `TodoRepository.kt`: Quản lý nguồn dữ liệu

#### 2. **View** (Giao diện)
- Hiển thị dữ liệu và nhận tương tác từ người dùng
- Trong dự án này:
  - `LoginActivity.kt`: Màn hình đăng nhập
  - `RegisterActivity.kt`: Màn hình đăng ký
  - `TodoListActivity.kt`: Màn hình danh sách công việc
  - Các file XML layout với DataBinding

#### 3. **ViewModel** (Mô hình giao diện)
- Kết nối giữa Model và View
- Quản lý trạng thái UI và logic xử lý
- Trong dự án này:
  - `LoginViewModel.kt`: Xử lý logic đăng nhập
  - `RegisterViewModel.kt`: Xử lý logic đăng ký
  - `TodoViewModel.kt`: Xử lý logic quản lý công việc

## DataBinding

DataBinding cho phép liên kết dữ liệu trực tiếp giữa ViewModel và View trong file XML:

```xml
<data>
    <variable
        name="viewModel"
        type="com.example.myapplication.viewmodel.LoginViewModel" />
</data>

<!-- Two-way binding -->
<EditText
    android:text="@={viewModel.username}" />
```

## LiveData và State Management

- **LiveData**: Observable data holder giúp View tự động cập nhật khi dữ liệu thay đổi
- **MutableLiveData**: Phiên bản có thể thay đổi của LiveData

```kotlin
private val _loginResult = MutableLiveData<LoginResult>()
val loginResult: LiveData<LoginResult> = _loginResult

// Trong Activity
viewModel.loginResult.observe(this) { result ->
    // Xử lý kết quả
}
```

## Cơ Sở Dữ Liệu SQLite với Room

Room là thư viện ORM giúp làm việc với SQLite dễ dàng hơn:

### Entity
```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String
)
```

### DAO (Data Access Object)
```kotlin
@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
}
```

### Database
```kotlin
@Database(entities = [User::class, Todo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun todoDao(): TodoDao
}
```

## Coroutines

Sử dụng Kotlin Coroutines để xử lý bất đồng bộ:

```kotlin
viewModelScope.launch {
    try {
        val user = repository.loginUser(username, password)
        _loginResult.value = LoginResult.Success(user)
    } catch (e: Exception) {
        _loginResult.value = LoginResult.Error(e.message)
    }
}
```

## Cấu Trúc Dự Án

```
app/src/main/java/com/example/myapplication/
├── data/
│   ├── dao/
│   │   ├── UserDao.kt
│   │   └── TodoDao.kt
│   ├── database/
│   │   └── AppDatabase.kt
│   ├── model/
│   │   ├── User.kt
│   │   └── Todo.kt
│   └── repository/
│       ├── UserRepository.kt
│       └── TodoRepository.kt
├── ui/
│   ├── adapter/
│   │   └── TodoAdapter.kt
│   ├── LoginActivity.kt
│   ├── RegisterActivity.kt
│   └── TodoListActivity.kt
└── viewmodel/
    ├── LoginViewModel.kt
    ├── RegisterViewModel.kt
    ├── TodoViewModel.kt
    └── ViewModelFactory.kt
```

## Chức Năng Ứng Dụng

### 1. Đăng Ký Tài Khoản
- Nhập username, email, password
- Validate dữ liệu đầu vào
- Kiểm tra username đã tồn tại
- Lưu vào SQLite database

### 2. Đăng Nhập
- Nhập username và password
- Xác thực với database
- Chuyển đến màn hình danh sách công việc

### 3. Quản Lý Công Việc
- Thêm công việc mới
- Đánh dấu hoàn thành/chưa hoàn thành
- Xóa công việc
- Hiển thị danh sách theo thời gian

## Ưu Điểm Của MVVM

1. **Tách biệt rõ ràng**: Logic nghiệp vụ tách khỏi UI
2. **Dễ test**: ViewModel có thể test độc lập
3. **Tái sử dụng**: ViewModel có thể dùng cho nhiều View
4. **Lifecycle-aware**: ViewModel tự động xử lý lifecycle
5. **DataBinding**: Giảm boilerplate code

## Cách Chạy Ứng Dụng

1. Mở project trong Android Studio
2. Sync Gradle
3. Chạy trên emulator hoặc thiết bị thật
4. Đăng ký tài khoản mới
5. Đăng nhập và quản lý công việc

## Công Nghệ Sử Dụng

- **Kotlin**: Ngôn ngữ lập trình
- **Room**: SQLite ORM
- **LiveData**: Observable data holder
- **ViewModel**: Quản lý UI state
- **DataBinding**: Liên kết dữ liệu
- **Coroutines**: Xử lý bất đồng bộ
- **Material Design**: Giao diện đẹp mắt
