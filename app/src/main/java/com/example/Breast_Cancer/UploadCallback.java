package com.example.Breast_Cancer;

public interface UploadCallback {
    void onSuccess(String responseBody);
    void onError(Exception e);
}
