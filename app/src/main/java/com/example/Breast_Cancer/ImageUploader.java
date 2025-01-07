package com.example.Breast_Cancer;

import android.graphics.Bitmap;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class ImageUploader {
    private static final String TAG = "ImageUploader";
    public static void uploadImageAsync(Bitmap bitmap, String uploadUrl, UploadCallback callback) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] imageBytes = stream.toByteArray();

                RequestBody reqFile = RequestBody.create(
                        imageBytes,
                        MediaType.parse("image/jpeg")
                );

                MultipartBody.Part body = MultipartBody.Part.createFormData(
                        "file",
                        "upload.jpg",
                        reqFile
                );

                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(body)
                        .build();

                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient.Builder()
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "Upload failed: " + e.getMessage(), e);
                        if (callback != null) {
                            callback.onError(e);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {

                            String msg = "Server error: " + response.code();
                            Log.e(TAG, msg);
                            if (callback != null) {
                                callback.onError(new Exception(msg));
                            }
                            return;
                        }

                        String responseBody = response.body().string();
                        Log.d(TAG, "Upload success. Body: " + responseBody);

                        if (callback != null) {
                            callback.onSuccess(responseBody);

                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception in upload thread: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
}
