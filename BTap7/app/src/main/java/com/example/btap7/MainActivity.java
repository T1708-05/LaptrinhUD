package com.example.btap7;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity - Upload ảnh lên server bằng Retrofit Multipart
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    // Views
    private EditText editUserName;
    private ImageView imgChoose, imgMultipart;
    private Button btnChoose, btnUpload;
    private TextView tvUsername;

    // Uri của ảnh đã chọn
    private Uri mUri;

    // ActivityResultLauncher để mở gallery
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ánh xạ view
        AnhXa();

        // Khởi tạo gallery launcher
        initGalleryLauncher();

        // Xử lý sự kiện click imgChoose
        imgChoose.setOnClickListener(v -> checkPermission());

        // Xử lý sự kiện click btnChoose
        btnChoose.setOnClickListener(v -> checkPermission());

        // Xử lý sự kiện click btnUpload
        btnUpload.setOnClickListener(v -> {
            if (mUri != null) {
                UploadImage1();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Ánh xạ các view
     */
    private void AnhXa() {
        editUserName = findViewById(R.id.editUserName);
        imgChoose = findViewById(R.id.imgChoose);
        imgMultipart = findViewById(R.id.imgMultipart);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        tvUsername = findViewById(R.id.tvUsername);
    }

    /**
     * Khởi tạo ActivityResultLauncher để nhận ảnh từ gallery
     */
    private void initGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        mUri = result.getData().getData();
                        // Hiển thị ảnh đã chọn lên imgChoose
                        imgChoose.setImageURI(mUri);
                        Toast.makeText(this, "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Kiểm tra và xin quyền truy cập storage
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (API 33+)
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        } else {
            // Android 12 trở xuống
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        }
    }

    /**
     * Mở gallery để chọn ảnh
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * Xử lý kết quả xin quyền
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Cần cấp quyền để chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Upload ảnh lên server
     */
    private void UploadImage1() {
        // Hiển thị progress dialog
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang upload...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Lấy username từ EditText
        String username = editUserName.getText().toString().trim();
        if (username.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Vui lòng nhập username", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo RequestBody cho username
        RequestBody requestBodyUsername = RequestBody.create(
                MediaType.parse("text/plain"),
                username
        );

        // Lấy real path từ Uri
        String realPath = RealPathUtil.getRealPath(this, mUri);
        Log.d(TAG, "Real path: " + realPath);

        if (realPath == null || realPath.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Không thể lấy đường dẫn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo File từ real path
        File file = new File(realPath);
        if (!file.exists()) {
            progressDialog.dismiss();
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo RequestBody cho file ảnh
        RequestBody requestBodyFile = RequestBody.create(
                MediaType.parse("image/*"),
                file
        );

        // Tạo MultipartBody.Part với key "avatar"
        MultipartBody.Part avatar = MultipartBody.Part.createFormData(
                "avatar",
                file.getName(),
                requestBodyFile
        );

        // Gọi API upload
        ServiceAPI serviceAPI = RetrofitClient.getServiceAPI();
        Call<List<ImageUpload>> call = serviceAPI.upload(requestBodyUsername, avatar);

        // Log URL để debug
        Log.d(TAG, "Upload URL: " + call.request().url());
        Log.d(TAG, "Username: " + username);
        Log.d(TAG, "File name: " + file.getName());

        call.enqueue(new Callback<List<ImageUpload>>() {
            @Override
            public void onResponse(@NonNull Call<List<ImageUpload>> call,
                                   @NonNull Response<List<ImageUpload>> response) {
                progressDialog.dismiss();

                // Log chi tiết response
                Log.d(TAG, "Response code: " + response.code());
                Log.d(TAG, "Response message: " + response.message());
                
                if (response.isSuccessful() && response.body() != null) {
                    List<ImageUpload> list = response.body();
                    Log.d(TAG, "Response body size: " + list.size());
                    
                    if (!list.isEmpty()) {
                        ImageUpload imageUpload = list.get(0);

                        // Hiển thị username
                        tvUsername.setText("Username: " + imageUpload.getUsername());

                        // Load ảnh avatar từ server bằng Glide
                        Glide.with(MainActivity.this)
                                .load(imageUpload.getAvatar())
                                .into(imgMultipart);

                        Toast.makeText(MainActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Upload success: " + imageUpload.getAvatar());
                    }
                } else {
                    // Log error body
                    try {
                        String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(MainActivity.this, 
                                "Upload thất bại: " + response.code() + " - " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        Toast.makeText(MainActivity.this, "Upload thất bại: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ImageUpload>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                String errorMsg = "Gọi API thất bại: " + t.getMessage();
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
                t.printStackTrace();
            }
        });
    }
}
