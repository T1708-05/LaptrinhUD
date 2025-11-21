package com.example.retrofit2.api;

import com.example.retrofit2.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // Thử các endpoint có thể có
    @GET("categories")
    Call<List<Category>> getCategoryAll();
    
    @GET("categories.php")
    Call<List<Category>> getCategoryPhp();
    
    @GET("api/categories")
    Call<List<Category>> getApiCategories();
    
    @GET("category")
    Call<List<Category>> getCategory();
}
