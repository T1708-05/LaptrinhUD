package com.example.retrofit2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.retrofit2.adapter.CategoryAdapter;
import com.example.retrofit2.api.ApiService;
import com.example.retrofit2.api.RetrofitClient;
import com.example.retrofit2.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    RecyclerView rcCate;
    // Khai báo Adapter
    CategoryAdapter categoryAdapter;
    ApiService apiService;
    List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AnhXa();
        GetCategory(); // Load dữ liệu cho category
    }

    private void AnhXa() {
        // Ánh xạ
        rcCate = (RecyclerView) findViewById(R.id.rc_category);
    }

    private void GetCategory() {
        Log.d("logg", "GetCategory() started");
        Toast.makeText(this, "Đang tải dữ liệu...", Toast.LENGTH_SHORT).show();
        
        // Gọi Interface trong APIService
        apiService = RetrofitClient.getRetrofit().create(ApiService.class);
        
        // Thử endpoint đầu tiên: categories
        tryEndpoint(apiService.getCategoryAll(), "categories");
    }
    
    private void tryEndpoint(Call<List<Category>> call, String endpointName) {
        Log.d("logg", "Trying endpoint: " + endpointName);
        Log.d("logg", "Request URL: " + call.request().url());
        
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                Log.d("logg", "onResponse [" + endpointName + "] - Code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body();
                    Log.d("logg", "SUCCESS! Loaded " + categoryList.size() + " items from: " + endpointName);
                    Toast.makeText(MainActivity.this, "✓ Thành công! " + categoryList.size() + " items", Toast.LENGTH_LONG).show();
                    
                    // Khởi tạo Adapter
                    categoryAdapter = new CategoryAdapter(MainActivity.this, categoryList);
                    rcCate.setHasFixedSize(true);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                            getApplicationContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false);
                    rcCate.setLayoutManager(layoutManager);
                    rcCate.setAdapter(categoryAdapter);
                    categoryAdapter.notifyDataSetChanged();
                } else {
                    int statusCode = response.code();
                    Log.e("logg", "Failed [" + endpointName + "] - Code: " + statusCode);
                    
                    // Nếu 404, thử endpoint khác
                    if (statusCode == 404) {
                        Toast.makeText(MainActivity.this, "Thử endpoint khác...", Toast.LENGTH_SHORT).show();
                        if (endpointName.equals("categories")) {
                            tryEndpoint(apiService.getCategoryPhp(), "categories.php");
                        } else if (endpointName.equals("categories.php")) {
                            tryEndpoint(apiService.getApiCategories(), "api/categories");
                        } else if (endpointName.equals("api/categories")) {
                            tryEndpoint(apiService.getCategory(), "category");
                        } else {
                            Toast.makeText(MainActivity.this, "Không tìm thấy endpoint nào hoạt động!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Lỗi " + statusCode, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("logg", "API Error [" + endpointName + "]: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}