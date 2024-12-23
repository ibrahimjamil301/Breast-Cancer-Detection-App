package com.example.graduation_project;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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


    /**
     * يرفع الصورة بشكل لا متزامن (Asynchronous) إلى السيرفر
     * @param bitmap        الصورة (Bitmap) المراد رفعها
     * @param uploadUrl     رابط السيرفر (مثل http://192.168.1.7:5000/predict)
     * @param callback      كائن Callback يبلّغك عن النجاح أو الخطأ
     */
    public static void uploadImageAsync(Bitmap bitmap, String uploadUrl, UploadCallback callback) {
        // 1) إنشاء خيط خلفي للتعامل مع ضغط الصورة
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            try {
                // 2) ضغط الصورة في الخلفية (avoid main thread block)
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // تقلل الحجم إلى 70% مثلاً:
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] imageBytes = stream.toByteArray();

                // 3) تحضير RequestBody
                RequestBody reqFile = RequestBody.create(
                        imageBytes,
                        MediaType.parse("image/jpeg")
                );

                // لاحظ اسم الحقل "image" يطابق ما يحتاجه سيرفرك:
                MultipartBody.Part body = MultipartBody.Part.createFormData(
                        "file",          // اسم الحقل
                        "upload.jpg",     // اسم الملف
                        reqFile
                );

                // 4) بناء الـMultipart body
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(body)
                        .build();

                // 5) بناء الـ Request (POST)
                Request request = new Request.Builder()
                        .url(uploadUrl)
                        .post(requestBody)
                        .build();

                // 6) كائن OkHttpClient (يمكن إضافة Timeouts لو أحببت)
                OkHttpClient client = new OkHttpClient.Builder()
                        .build();

                // 7) تنفيذ الطلب بشكل غير متزامن
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
                            // رد السيرفر ليس 2xx
                            String msg = "Server error: " + response.code();
                            Log.e(TAG, msg);
                            if (callback != null) {
                                callback.onError(new Exception(msg));
                            }
                            return;
                        }

                        // نجاح
                        String responseBody = response.body().string();
                        Log.d(TAG, "Upload success. Body: " + responseBody);

                        if (callback != null) {
                            callback.onSuccess(responseBody);


                        }
                    }
                });
            } catch (Exception e) {
                // أي استثناء يحصل أثناء الضغط أو التحضير
                Log.e(TAG, "Exception in upload thread: " + e.getMessage(), e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });

        // ملاحظة: عادةً لا تغلق الـ ExecutorService مباشرة
        // لأنك قد تريد إعادة استخدامه. إذا أردت ذلك:
        // executor.shutdown();
    }
}
