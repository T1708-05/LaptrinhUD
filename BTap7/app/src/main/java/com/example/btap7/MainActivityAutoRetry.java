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
 * MainActivity với Auto Retry - Tự động thử nhiều endpoint nếu gặp 404
 * 
 * HƯỚNG DẪN: Đổi tên file này thành MainActivity.java để sử dụng
 * (backup MainActivity.java cũ trước)
 */
public class MainActivityAutoRetry extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText editUserName;
    private ImageView imgChoose, imgMultipart;
    private Button btnChoose, btnUpload;
    private TextView tvUsername;
    private Uri mUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    
    private int currentEndpointIndex = 0;
    private String[] endpoints = {"updateimages.php", "upload.php", "uploadimage.php"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();
        initGalleryLauncher();

        imgChoose.setOnClickListener(v -> checkPermission());
        btnChoose.setOnClickListener(v -> checkPermission());
        btnUpload.setOnClickListener(v -> {
            if (mUri != null) {
                currentEndpointIndex = 0; // Reset về endpoint đầu tiên
                UploadImage1();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void AnhXa() {
        editUserName = findViewById(R.id.editUserName);
        imgChoose = findViewById(R.id.imgChoose);
        imgMultipart = findViewById(R.id.imgMultipart);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        tvUsername = findViewById(R.id.tvUsername);
    }

    private void initGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        mUri = result.getData().getData();
                        imgChoose.setImageURI(mUri);
                        Toast.makeText(this, "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        } else {
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

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

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
     * Upload với auto retry - tự động thử endpoint khác nếu gặp 404
     */
    private void UploadImage1() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang upload... (Endpoint " + (currentEndpointIndex + 1) + "/" + endpoints.length + ")");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String username = editUserName.getText().toString().trim();
        if (username.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Vui lòng nhập username", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBodyUsername = RequestBody.create(MediaType.parse("text/plain"), username);
        String realPath = RealPathUtil.getRealPath(this, mUri);
        
        Log.d(TAG, "Trying endpoint: " + endpoints[currentEndpointIndex]);
        Log.d(TAG, "Real path: " + realPath);

        if (realPath == null || realPath.isEmpty()) {
            progressDialog.dismiss();
            Toast.makeText(this, "Không thể lấy đường dẫn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(realPath);
        if (!file.exists()) {
            progressDialog.dismiss();
            Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBodyFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part avatar = MultipartBody.Part.createFormData("avatar", file.getName(), requestBodyFile);

        // Chọn endpoint dựa vào index
        ServiceAPI serviceAPI = RetrofitClient.getServiceAPI();
        Call<List<ImageUpload>> call;
        
        switch (currentEndpointIndex) {
            case 0:
                call = serviceAPI.upload(requestBodyUsername, avatar);
                break;
            case 1:
                call = serviceAPI.uploadAlternative(requestBodyUsername, avatar);
                break;
            case 2:
                call = serviceAPI.uploadImage(requestBodyUsername, avatar);
                break;
            default:
                progressDialog.dismiss();
                Toast.makeText(this, "Đã thử hết các endpoint, không có endpoint nào hoạt động", 
                        Toast.LENGTH_LONG).show();
                return;
        }

        Log.d(TAG, "Upload URL: " + call.request().url());

        call.enqueue(new Callback<List<ImageUpload>>() {
            @Override
            public void onResponse(@NonNull Call<List<ImageUpload>> call,
                                   @NonNull Response<List<ImageUpload>> response) {
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    progressDialog.dismiss();
                    List<ImageUpload> list = response.body();
                    
                    if (!list.isEmpty()) {
                        ImageUpload imageUpload = list.get(0);
                        tvUsername.setText("Username: " + imageUpload.getUsername());
                        Glide.with(MainActivityAutoRetry.this)
                                .load(imageUpload.getAvatar())
                                .into(imgMultipart);

                        Toast.makeText(MainActivityAutoRetry.this, 
                                "Upload thành công với endpoint: " + endpoints[currentEndpointIndex], 
                                Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Upload success: " + imageUpload.getAvatar());
                    }
                } else if (response.code() == 404) {
                    // Nếu 404, thử endpoint tiếp theo
                    Log.e(TAG, "404 Not Found for endpoint: " + endpoints[currentEndpointIndex]);
                    currentEndpointIndex++;
                    
                    if (currentEndpointIndex < endpoints.length) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivityAutoRetry.this, 
                                "Endpoint không tồn tại, thử endpoint khác...", 
                                Toast.LENGTH_SHORT).show();
                        // Thử lại với endpoint mới
                        UploadImage1();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivityAutoRetry.this, 
                                "Đã thử hết " + endpoints.length + " endpoint, không có endpoint nào hoạt động", 
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog.dismiss();
                    try {
                        String errorBody = response.errorBody() != null ? 
                                response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error body: " + errorBody);
                        Toast.makeText(MainActivityAutoRetry.this, 
                                "Upload thất bại: " + response.code() + " - " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                        Toast.makeText(MainActivityAutoRetry.this, 
                                "Upload thất bại: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ImageUpload>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                String errorMsg = "Gọi API thất bại: " + t.getMessage();
                Toast.makeText(MainActivityAutoRetry.this, errorMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "API call failed: " + t.getMessage(), t);
            }
        });
    }
}
