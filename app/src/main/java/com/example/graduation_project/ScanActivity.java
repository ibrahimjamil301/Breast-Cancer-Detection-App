package com.example.graduation_project;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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


public class ScanActivity extends AppCompatActivity {

     private String classificationResult = null;

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
                if (classificationResult == null) {
                    // لم يتم رفع الصورة أو لم يصل الرد بعد.
                    // يمكنك إعلام المستخدم بضرورة رفع الصورة قبل الاستمرار
                    Toast.makeText(ScanActivity.this,
                            "No result yet! Take a photo or wait a while.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // إذا وصلت قيمة ما في classificationResult
                if (classificationResult.equalsIgnoreCase("Benign")) {
                    Intent intent = new Intent(ScanActivity.this, ResultActivity.class);
                    startActivity(intent);

                } else if (classificationResult.equalsIgnoreCase("Malignant")) {
                    Intent intent = new Intent(ScanActivity.this, ResultActivity2.class);
                    startActivity(intent);
                } else {
                    // لو فرضاً رجعت قيمة مختلفة
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

                        sendImageToServer(imageBitmap);
                    }
                }
            } else if (requestCode == GALLERY_REQUEST_CODE) {

                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);

                    sendImageToServer(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // اضغط الصورة بصيغة JPEG وبجودة 100% (يمكنك تقليل الجودة للحدّ من حجم الملف)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    private void sendImageToServer(Bitmap bitmap) {
        // 1) تحويل الـ Bitmap إلى مصفوفة بايت
        byte[] imageBytes = bitmapToByteArray(bitmap);

        // 2) بناء الـ RequestBody من نوع image/jpeg
        RequestBody requestFile = RequestBody.create(
                imageBytes,
                MediaType.parse("image/jpeg")
        );

        // 3) إضافة الجزء الخاص بالملف (FormData)
        MultipartBody.Part body = MultipartBody.Part.createFormData(
                "file",         // اسم الحقل الذي سيرسله للفلاسك (مثل request.files['image'])
                "upload.jpg",    // اسم الملف (صوري، ليس إلزاميًا)
                requestFile
        );

        // تستطيع هنا إضافة حقول إضافية لو كانت مطلوبة في الـ Flask
        // (مثلاً اسم المستخدم أو أي بيانات نصية أخرى)

        // 4) بناء كائن OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();


        // 5) بناء الـ requestBody الشامل (multipart)
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(body)
                .build();

        // 6) بناء الـ Request وتحديد الرابط (URL)
        // يجب تغيير الرابط لعنوان السيرفر (قد يكون http://192.168.x.x:5000/predict أو http://10.0.2.2:5000/predict إذا كنت على Emulator)
        Request request = new Request.Builder()
                .url("http://192.168.1.7:5000/predict")
                .post(requestBody)
                .build();

        // 7) تنفيذ الطلب بشكل غير متزامن (Asynchronous) حتى لا تعلق واجهة المستخدم
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // في حال فشل الاتصال أو أي خطأ بالشبكة
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.e("HTTP_ERROR", "Response code: " + response.code());
                    runOnUiThread(() -> Toast.makeText(ScanActivity.this,
                            "Server error, code = " + response.code(),
                            Toast.LENGTH_SHORT).show());

                // قراءة محتوى الرد (عادة يكون جيسون)
                    assert response.body() != null;
                    String responseBody = response.body().string();

                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        double confidence = jsonObject.getDouble("confidence");
                        // بافتراض أن السيرفر يعيد حقل "label" بقيمة "Benign" أو "Malignant"
                        String label = jsonObject.getString("label");

                        // احفظ النتيجة في المتغيّر العام
                        classificationResult = label;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ScanActivity.this,
                                        "Result: " + label + " (Confidence: " + confidence + ")",
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



}