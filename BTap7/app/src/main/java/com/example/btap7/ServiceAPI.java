package com.example.btap7;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Interface định nghĩa các API endpoint
 */
public interface ServiceAPI {
    
    /**
     * Upload ảnh lên server - endpoint updateimages.php
     * @param username RequestBody chứa username
     * @param avatar MultipartBody.Part chứa file ảnh
     * @return Call trả về List<ImageUpload>
     */
    @Multipart
    @POST("updateimages.php")
    Call<List<ImageUpload>> upload(
            @Part("username") RequestBody username,
            @Part MultipartBody.Part avatar
    );
    
    /**
     * Upload ảnh lên server - endpoint upload.php (phương án 2)
     * @param username RequestBody chứa username
     * @param avatar MultipartBody.Part chứa file ảnh
     * @return Call trả về List<ImageUpload>
     */
    @Multipart
    @POST("upload.php")
    Call<List<ImageUpload>> uploadAlternative(
            @Part("username") RequestBody username,
            @Part MultipartBody.Part avatar
    );
    
    /**
     * Upload ảnh lên server - endpoint uploadimage.php (phương án 3)
     * @param username RequestBody chứa username
     * @param avatar MultipartBody.Part chứa file ảnh
     * @return Call trả về List<ImageUpload>
     */
    @Multipart
    @POST("uploadimage.php")
    Call<List<ImageUpload>> uploadImage(
            @Part("username") RequestBody username,
            @Part MultipartBody.Part avatar
    );
}
