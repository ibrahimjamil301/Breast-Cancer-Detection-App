package com.example.graduation_project;

public interface UploadCallback {
    void onSuccess(String responseBody);
    void onError(Exception e);
}
