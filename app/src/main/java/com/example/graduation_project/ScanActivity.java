package com.example.graduation_project;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import com.example.graduation_project.ImageUploader;
import com.example.graduation_project.UploadCallback;


public class ScanActivity extends AppCompatActivity {

      String classificationResult = null;

     private static final int CAMERA_REQUEST_CODE = 100;
     private static final int GALLERY_REQUEST_CODE = 200;
     Button Start_btn ;
     TextView captureImgBtn;
     ImageView home_btn,profile_btn, imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        captureImgBtn = findViewById(R.id.capture_img);
        imageView = findViewById(R.id.upload_area);
     //   Test_btn = findViewById(R.id.test_btn);
        Start_btn = findViewById(R.id.start_btn);
        home_btn = findViewById(R.id.home_icon);
        profile_btn = findViewById(R.id.profile_icon);

        /*

        Test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanActivity.this,ResultActivity2.class);
                startActivity(intent);

            }
        });

        */

        Start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // التحقق أولاً: إذا النتيجة فارغة أو لم يتم تعيينها
                if (classificationResult == null || classificationResult.trim().isEmpty()) {
                    Toast.makeText(ScanActivity.this,
                            "No result yet! Please capture an image or wait a while.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // التحقق من نوع النتيجة
                if (classificationResult.equalsIgnoreCase("Benign")) {
                    // التوجيه لـ ResultActivity
                    Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                    startActivity(intent);

                } else if (classificationResult.equalsIgnoreCase("Malignant")) {
                    // التوجيه لـ ResultActivity2
                    Intent intent = new Intent(ScanActivity.this, ResultActivity2.class);
                    startActivity(intent);

                } else {
                    // قيمة غير معروفة
                    Toast.makeText(ScanActivity.this,
                            "Unknown value: " + classificationResult,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent = new Intent(ScanActivity.this,HomeActivity.class);
                startActivity(new_intent);
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_intent1 = new Intent(ScanActivity.this,ProfileActivity.class);
                new_intent1.putExtra("fromHome", false);
                startActivity(new_intent1);
            }
        });

        captureImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageSourceDialog();

            }
        });


    }
    private void showImageSourceDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScanActivity.this);
        builder.setTitle("please chose ");
        builder.setItems(new CharSequence[] {"Open Camera", "Open Gallery"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

                        } else if (which == 1) {

                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                        }
                    }
                });
        builder.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {

                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imageView.setImageBitmap(imageBitmap);

                        sendImage(imageBitmap);


                    }
                }
            } else if (requestCode == GALLERY_REQUEST_CODE) {

                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);

                    sendImage(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendImage(Bitmap bitmap) {
        // مثال على رابط السيرفر
        String serverUrl = "http://192.168.1.7:5000/predict";

        ImageUploader.uploadImageAsync(bitmap, serverUrl, new UploadCallback() {
            @Override
            public void onSuccess(String responseBody) {
                // ينفّذ في Thread الخلفية (تحذير)
                // لو أردت تعديل الواجهة (UI)، استخدم runOnUiThread
                runOnUiThread(() -> {
                    // معالجة النتيجة
                    Toast.makeText(ScanActivity.this,
                            "Upload success: " + responseBody,
                            Toast.LENGTH_LONG).show();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String label = json.optString("label", "");
                        double confidence = json.optDouble("confidence", 0.0);

                        // تعيين قيمة classificationResult اعتمادًا على label
                        classificationResult = label; // قد تكون "Benign" أو "Malignant"

                        // إذا أردت إظهار القيمة
                        // Toast.makeText(ScanActivity.this,
                        //         "classificationResult = " + classificationResult,
                        //         Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ScanActivity.this,
                                "JSON parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                    // يمكنك هنا أو في زرّ آخر استخدام classificationResult للانتقال:
                    // if ("Benign".equalsIgnoreCase(classificationResult)) { ... }
                });
            }

                    // مثال: لو لديك parse JSON
                    // then navigate to other activity, etc.


            @Override
            public void onError(Exception e) {
                // نفس الشيء، نفذ على الرئيسية لو أردت:
                runOnUiThread(() -> {
                    Toast.makeText(ScanActivity.this,
                            "Upload error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }





}