package com.example.retrofit2.api;

import com.example.retrofit2.model.Category;
import com.example.retrofit2.model.ProductResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // Thử các endpoint có thể có
    @GET("categories")
    Call<List<Category>> getCategoryAll();
    
    // Hoặc có thể là:
    // @GET("api/categories")
    // @GET("category")
}
