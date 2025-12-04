package com.example.btap7;

import com.google.gson.annotations.SerializedName;

/**
 * Model class cho response từ API upload
 */
public class ImageUpload {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("avatar")
    private String avatar;

    // Constructor
    public ImageUpload() {
    }

    public ImageUpload(int id, String username, String avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
